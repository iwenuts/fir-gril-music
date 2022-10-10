package music.mp3.song.app.song.music.tube.ztools;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.player.PlayMode;
import music.mp3.song.app.song.music.tube.arate.RateBean;

import java.util.List;

public class vPrefsUtils {
    //app打开次数
    private static final String OPEN_COUNT = "open";
    //    private static final String OLD_USER = "old";
    private static final String DOWNLOAD = "download";
    private static final String RATE_BEAN = "rate";
    private static final String CN_USER = "country";
    private static final String BAN_USER = "ban";
    private static final String CFG_COUNT = "config_count";
    private static final String DL_AD_COUNT = "dl_ad_count";
    private static final String SE_AD_COUNT = "se_ad_count";

    private static final String GET_REFERER = "done";
    private static final String GET_REFERER_API = "api_done";

    private static final String SPEC = "spec";
    private static final String TIME = "time";
    private static final String CONFIG = "config";
    private static final String PLAY_MODE = "playMode";
    public static int sDownloadSuccessCount = 0;
    private static final String NORMAL_USER = "common";
    public static final int INVALID = -1;

    public static int getConfigCount() {
        return getPrefs().getInt(CFG_COUNT, 0);
    }

    public static void setConfigCount(int openCount) {
        getPrefs().edit().putInt(CFG_COUNT, openCount).apply();
    }

    public static boolean getBanUser() {
        return getPrefs().getBoolean(BAN_USER, false);
    }

    public static void setBanUser(boolean user) {
        getPrefs().edit().putBoolean(BAN_USER, user).apply();
    }

    public static boolean getCnUser() {
        return getPrefs().getBoolean(CN_USER, false);
    }

    public static void setCnUser(boolean cnUser) {
        getPrefs().edit().putBoolean(CN_USER, cnUser).apply();
    }

    public static int getNormalUser() {
        return getPrefs().getInt(NORMAL_USER, INVALID);
    }

    public static void setNormalUser(int normal) {
        getPrefs().edit().putInt(NORMAL_USER, normal).apply();
    }

    public static RateBean getRateBean() {
        SharedPreferences s = getPrefs();
        if (TextUtils.isEmpty(s.getString(RATE_BEAN, ""))) {
            return null;
        }
        return new Gson().fromJson(s.getString(RATE_BEAN, ""), RateBean.class);
    }

    public static void setRateBean(RateBean bean) {
        SharedPreferences s = getPrefs();
        s.edit().putString(RATE_BEAN, new Gson().toJson(bean)).apply();
    }

    public static int getOpenCount() {
        SharedPreferences s = getPrefs();
        return s.getInt(OPEN_COUNT, 0);
    }

    public static void setOpenCount(int openCount) {
        SharedPreferences s = getPrefs();
        s.edit().putInt(OPEN_COUNT, openCount).apply();
    }

    public static List<Music> getDownloadCache() {
        SharedPreferences s = getPrefs();
        if (TextUtils.isEmpty(s.getString(DOWNLOAD, ""))) {
            return null;
        }
        return new Gson().fromJson(s.getString(DOWNLOAD, ""), new TypeToken<List<Music>>() {
        }.getType());
    }

    private static Gson gson = new Gson();

    public synchronized static void setDownloadCache(List<Music> list) {
        SharedPreferences s = getPrefs();
        try {
            s.edit().putString(DOWNLOAD, gson.toJson(list)).apply();
        }catch (Exception e){

        }
    }


    public static boolean getRefererDone() {
        SharedPreferences s = getPrefs();
        return s.getBoolean(GET_REFERER, false);
    }

    public static void setReferDone(boolean isRefer) {
        SharedPreferences s = getPrefs();
        s.edit().putBoolean(GET_REFERER, isRefer).commit();
    }

    public static boolean getRefererApiDone() {
        SharedPreferences s = getPrefs();
        return s.getBoolean(GET_REFERER_API, false);
    }

    public static void setReferApiDone(boolean isRefer) {
        SharedPreferences s = getPrefs();
        s.edit().putBoolean(GET_REFERER_API, isRefer).apply();
    }

    public static boolean getIsSuper() {
        SharedPreferences s = getPrefs();
        return s.getBoolean(SPEC, false);
    }

    public static void setIsSuper(boolean isSuper) {
        SharedPreferences s = getPrefs();
        s.edit().putBoolean(SPEC, isSuper).apply();
    }

    public static boolean getTs() {
        SharedPreferences s = getPrefs();
        return s.getBoolean(TIME, false);
    }

    public static void setTs(boolean isSuper) {
        SharedPreferences s = getPrefs();
        s.edit().putBoolean(TIME, isSuper).apply();
    }

    public static Config getConfig() {
        SharedPreferences s = getPrefs();
        if (TextUtils.isEmpty(s.getString(CONFIG, ""))) {
            return null;
        }
        return new Gson().fromJson(s.getString(CONFIG, ""), new TypeToken<Config>() {
        }.getType());
    }

    public static void setConfig(Config config) {
        try {
            SharedPreferences s = getPrefs();
            s.edit().putString(CONFIG, new Gson().toJson(config)).apply();
        } catch (Exception e) {
        }
    }

    private static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(MusicApp.getInstance());
    }

    public static PlayMode lastPlayMode() {
        String playModeName = getPrefs().getString(PLAY_MODE, null);
        if (playModeName != null) {
            return PlayMode.valueOf(playModeName);
        }
        return PlayMode.getDefault();
    }

    public static void setPlayMode(PlayMode playMode) {
        getPrefs().edit().putString(PLAY_MODE, playMode.name()).commit();
    }

    public static long dlNextLong() {
        SharedPreferences s = getPrefs();
        long cnt = s.getLong(DL_AD_COUNT, 0);
        s.edit().putLong(DL_AD_COUNT, cnt + 1).apply();
        return cnt;
    }

    public static long seNextLong() {
        SharedPreferences s = getPrefs();
        long cnt = s.getLong(SE_AD_COUNT, 0);
        s.edit().putLong(SE_AD_COUNT, cnt + 1).apply();
        return cnt;
    }

}
