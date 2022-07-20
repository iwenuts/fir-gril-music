package song.downloader.music.tube.firebase;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import encrypt.pck.JiaMiEncrypted;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import song.downloader.music.tube.MusicApp;
import song.downloader.music.tube.BuildConfig;
import song.downloader.music.tube.ztools.MathConst;
import song.downloader.music.tube.ztools.vPrefsUtils;
import song.downloader.music.tube.ztools.Utils;


public class Referrer {
    private static volatile boolean sIsAd = false;//ad
    private static volatile boolean sIsTs = false;//ts
    private static volatile boolean sIsCnAllowed = false;
    private static volatile boolean sIsCnbanned = false;

    public static void initSuper() {
        sIsAd = vPrefsUtils.getIsSuper();
        sIsTs = vPrefsUtils.getTs();
        sIsCnAllowed = vPrefsUtils.getCnUser();
        sIsCnbanned = vPrefsUtils.getBanUser();
    }

    public static void setUacInstall(Context context, String devToken, String linkId) {
        final String s2sFrom = "s2s";
        FlurryEventReport.logUacEvent("uac_enter");

        //从ads api获取广告安装信息
        if (TextUtils.isEmpty(devToken) || TextUtils.isEmpty(linkId)) {
            Referrer.sendReferrer("", "0:not config", s2sFrom);
            return;
        }
        FlurryEventReport.logUacEvent("uac_begin");
        CampaignTrackTask campaignTrackTask = (CampaignTrackTask) new CampaignTrackTask(
                devToken,
                linkId,
                context,
                3,
                10000,
                30,
                (campaignId) -> {
                    //获取到google ads campaign信息后，执行业务逻辑
                    setSuper();
                    FlurryEventReport.logSentOpenSuper("s2s_ad", s2sFrom);
                    FlurryEventReport.logUacEvent("uac_ad");
                },
                (campaignId) -> {
                    //DDL触发失败的逻辑
                    FlurryEventReport.logUacEvent("uac_noad");
                }).execute();
    }

    public static void sendReferrer(String referrer, String reason, String from) {
        FlurryEventReport.logUacError(reason, referrer);
    }

    private static boolean isGooglePhone() {
        String brand = android.os.Build.BRAND;
        String model = android.os.Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;
        String phone = (brand + " " + model + " " + manufacturer).toLowerCase();

        if (phone.contains("google") || phone.contains("nexus") || phone.contains("pixel")) {
            return true;
        }
        return false;
    }

    public static boolean checkCountry(Context context) {
        String country4 = getPhoneCountry(context);
        String country3 = getSimCountry(context);
        country3 = TextUtils.isEmpty(country3) ? "" : country3.toLowerCase();
        country4 = TextUtils.isEmpty(country4) ? "" : country4.toLowerCase();
        if (TextUtils.isEmpty(country3) || TextUtils.isEmpty(country4) || !country3.equals(country4)) {
            return true;
        }
        String ban = JiaMiEncrypted.ban;
        if (!TextUtils.isEmpty(MusicApp.config.ban)) {
            ban = ban + "-" + MusicApp.config.ban;
        }
        if (ban.contains(country3)) {
            return true;
        }
        String localeCn = Utils.getlocaleCountry();
        if (!TextUtils.isEmpty(localeCn)) {
            localeCn = localeCn.toLowerCase();
            if (ban.contains(localeCn)) {
                return true;
            }
        }
        String language = Utils.getLocaleLanguage();
        if (!TextUtils.isEmpty(language)) {
            language = language.toLowerCase();
            String specialLanguage = JiaMiEncrypted.speciallanguage;
            if (!TextUtils.isEmpty(MusicApp.config.speciallanguage)) {
                specialLanguage = specialLanguage + "-" + MusicApp.config.speciallanguage;
            }
            if (specialLanguage.contains(language)) {
                return true;
            }
        }
        return false;
    }


    public static void setCountryFlag(Context context) {
        if (checkCountry(context)) {
            setBanUser();
            return;
        }

        if (MusicApp.config.level == 0) {
            setCnUser(true);
            return;
        }
        setCnUser(false);
        //非normal用户 ban
        if (!MusicApp.normalUser) {
            setBanUser();
            return;
        }
        if (isGooglePhone()) {
            setBanUser();
            return;
        }
        //无sim卡，或sim不等于network，ban
        String country4 = getPhoneCountry(context);
        String country3 = getSimCountry(context);
        country3 = TextUtils.isEmpty(country3) ? "" : country3.toLowerCase();
        country4 = TextUtils.isEmpty(country4) ? "" : country4.toLowerCase();
        if (TextUtils.isEmpty(country3) || TextUtils.isEmpty(country4)
                || !country3.equals(country4)) {
            setBanUser();
            return;
        }

        //特殊国家 ban
        String[] bans = MusicApp.config.ban.split("-");
        for (String ban : bans) {
            if (ban.equals(country3)) {
                setBanUser();
                return;
            }
        }

        //印度再检测locale
        String localeCn = Utils.getlocaleCountry();
        if (!TextUtils.isEmpty(localeCn)) {
            if (localeCn.toLowerCase().equals("in")) {
                setBanUser();
                return;
            }
        }

        if (getCountry(MusicApp.config.cnx, country3)) {
            setCnUser(true);
        }
    }

    /*
      +0us.gb +表示这些国家是allow的，0表示不用ts策略，1表示允许的国家应用 ts策略，2表示不允许的国家应用ts策略
      -0us.gb -表示这些国家是不允许的，
    */
    private static boolean getCountry(String countryCode, String country3) {
        boolean countryHit = false;
        if (!TextUtils.isEmpty(countryCode)) {
            String[] subCountryCodes = countryCode.split(",");
            for (String subCountryCode : subCountryCodes) {
                countryHit = false;
                if (!TextUtils.isEmpty(subCountryCode) && subCountryCode.length() >= 2) {
                    boolean revert = "-".equals(subCountryCode.substring(0, 1));
                    String level = subCountryCode.substring(1, 2);
                    String cn = subCountryCode.substring(2);
                    countryHit = hitCountry(cn, country3);
                    if (revert) {
                        countryHit = !countryHit;
                    }
                    if ("0".equals(level)) {
                        if ((revert && !countryHit) || (!revert && countryHit)) {
                            return countryHit;
                        }
                    } else if ("1".equals(level) && countryHit) {
                        countryHit = sIsTs;
                        break;
                    } else if ("2".equals(level) && !countryHit) {
                        countryHit = sIsTs;
                        break;
                    }
                }
            }
        }
        return countryHit;
    }

    private static boolean hitCountry(String cn, String country3) {
        if (TextUtils.isEmpty(cn)) {
            return false;
        }
        String[] countries = cn.split("\\.");
        for (String country : countries) {
            country = country.toLowerCase();
            if (TextUtils.isEmpty(country)) continue;
            if (country.equals(country3)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isSuper() {
        //wjtodo:
        if (BuildConfig.DEBUG) {
            return true;
        }
        return sIsAd;
    }

    public static void setSuper() {
        sIsAd = true;
        vPrefsUtils.setIsSuper(true);
    }


    public static void setBanUser() {
        sIsCnbanned = true;
        vPrefsUtils.setBanUser(true);
    }

    public static boolean isBanUser() {
        if (BuildConfig.DEBUG) {
            return false;
        }
        return sIsCnbanned;
    }

    public static void setCnUser(boolean allow) {
        sIsCnAllowed = allow;
        vPrefsUtils.setCnUser(allow);
    }

    public static boolean isCnUser() {
        return sIsCnAllowed;
    }

//    public static void setOldUser(boolean oldUser) {
//        sOldUser = oldUser;
//        PrefsUtils.setOldUser(oldUser ? 1 : 0);
//    }
//    public static boolean isOldUser() {
//        return sOldUser;
//    }

    public static void setTs(boolean allow) {
        sIsTs = allow;
        vPrefsUtils.setTs(allow);
    }


    public static boolean isAdmobOpen(String referrer) {
        if (referrer.startsWith("pcampaignid") && referrer.contains("youtubeads") && !referrer.contains("google")) {
            return true;
        } else if (referrer.startsWith("adsplayload=") && referrer.contains("conv=")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isFacebookOpen(String referrer) {
        // utm_source=appRecommend
        if (referrer.contains(stringToMD5(MusicApp.sContext.getPackageName()))) {
            return true;
        } else if (referrer.contains(MathConst.rec) && !referrer.contains("google")) {
            return true;
        }
        return false;

    }

    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }


    public static boolean isRoot() {
        boolean bool = false;

        try {
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())) {
                bool = false;
            } else {
                bool = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bool;
    }

    public static String getPhoneCountry(Context context) {
        String country = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getPhoneType()
                    != TelephonyManager.PHONE_TYPE_CDMA) {
                country = telephonyManager.getNetworkCountryIso();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return country;
    }

    public static String getSimCountry(Context context) {
        String country = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simCountry = telephonyManager.getSimCountryIso();
            country = simCountry.toLowerCase();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return country;
    }

}
