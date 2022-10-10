package music.mp3.song.app.song.music.tube.firebase;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.flurry.android.FlurryAgent;
import com.google.firebase.analytics.FirebaseAnalytics;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.ztools.vPrefsUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * note:
 * 1. key不能超过40个字符
 * 2. value只能是 String 和 Number，不能用Boolean
 */

public class FlurryEventReport {
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void init(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static void logSentReferFalse(String source, String from) {
        Map<String, String> bundle = new HashMap<>();
        bundle.put("from", source);
        bundle.put("ref_from", from);
        FlurryAgent.logEvent("logSentReferFalse", bundle);
    }

    public static void logSentUserInfo(String simCode, String phoneCode) {
        simCode = TextUtils.isEmpty(simCode) ? "" : simCode.toLowerCase();
        phoneCode = TextUtils.isEmpty(phoneCode) ? "" : phoneCode.toLowerCase();
        int cnStat;
        if (TextUtils.isEmpty(simCode) && TextUtils.isEmpty(phoneCode)) {
            cnStat = 0; //both empty
        } else if (!TextUtils.isEmpty(simCode) && !TextUtils.isEmpty(phoneCode)) {
            if (simCode.equals(phoneCode)) {
                cnStat = 2; //equal
            } else {
                cnStat = 3; //not empty but not equal
            }
        } else if (TextUtils.isEmpty(simCode)) {
            cnStat = 4; //sim empty
        } else {
            cnStat = 5; //net empty
        }
        int fake = MusicApp.normalUser ? 0 : 1;
        Map<String, String> bundle = new HashMap<>();
        bundle.put("cnStat", "" + cnStat);
        bundle.put("fake", "" + fake);
        bundle.put("phone", android.os.Build.MODEL);
        bundle.put("sim", simCode);
        bundle.put("net", phoneCode);
        FlurryAgent.logEvent("logSentUserInfo", bundle);
    }

    public static void sendIpCode1(String ipCode) {
        Map<String, String> bundle = new HashMap<>();
        bundle.put("country", ipCode);
        FlurryAgent.logEvent("ipCountry1", bundle);
    }

    public static void sendIpCode2(String ipCode) {
        Map<String, String> bundle = new HashMap<>();
        bundle.put("country", ipCode);
        FlurryAgent.logEvent("ipCountry2", bundle);
    }

    public static void logSentReferrer(String Referrer, String from) {
        Map<String, String> bundle = new HashMap<>();
        bundle.put("referrer", Referrer);
        bundle.put("ref_from", from);
        FlurryAgent.logEvent("logSentReferrer", bundle);
    }

    public static void logSentOpenSuper(String source, String from) {
        Map<String, String> bundle = new HashMap<>();
        bundle.put("from", source);
        bundle.put("ref_from", from);
        FlurryAgent.logEvent("logSentOpenApp", bundle);
    }

    public static void logDownloadSucc(int channel) {
        Map<String, String> params = new HashMap<>();
        params.put("channel", "" + channel);
        FlurryAgent.logEvent("downloadSucc", params);
    }

    public static void logDownloadFail(int channel) {
        Map<String, String> params = new HashMap<>();
        params.put("channel", "" + channel);
        FlurryAgent.logEvent("downloadFail", params);
    }

    public static void logListen(int channel) {
        Map<String, String> params = new HashMap<>();
        params.put("channel", "" + channel);
        FlurryAgent.logEvent("listen", params);
    }

    public static void logFbError(String adid, int errno, String errmsg) {
//        Map<String,String> params = new HashMap<>();
//        params.put("adid", adid);
//        params.put("errno", errno);
//        params.put("errmsg", errmsg);
//        FlurryAgent.logEvent("fbAdError", params);
    }

    public static void logUacError(String reason, String extraInfo) {
        Map<String, String> params = new HashMap<>();
        params.put("reason", reason);
        params.put("extra", extraInfo);
        FlurryAgent.logEvent("uacError", params);
    }

    public static void logUacEvent(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        FlurryAgent.logEvent("uacEvent", params);
    }

    public static void pageraction(String pagerAction) {
        Map<String, String> params = new HashMap<>();
        params.put("action", pagerAction);
        FlurryAgent.logEvent("pagerAction", params);
    }

    public static void rateShow() {
        Map<String, String> params = new HashMap<>();
        params.put("action", "show");
        int cfgCount = vPrefsUtils.getConfigCount();
        params.put("isFirst", (cfgCount == 0) + "");
        FlurryAgent.logEvent("rate", params);
    }

    public static void rateClick(String five) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "click");
        int cfgCount = vPrefsUtils.getConfigCount();
        params.put("isFirst", (cfgCount == 0) + "");
        params.put("level", five);
        FlurryAgent.logEvent("rateClick", params);
    }

    public static void moveFailMessage(String message) {
        Map<String, String> params = new HashMap<>();
        params.put("message", message);
        FlurryAgent.logEvent("moveFailMessage", params);
    }

    public static void createResult(String s) {
        Map<String, String> params = new HashMap<>();
        params.put("category", s);
        FlurryAgent.logEvent("ResultCategory", params);
    }

    public static void ipBlock(boolean status, boolean banUser) {
        Map<String, String> params = new HashMap<>();
        params.put("status", status + "");
        params.put("banUser", banUser + "");
        FlurryAgent.logEvent("IpBlock", params);

    }

    public static void downplayclick() {
        try {
            Bundle bundle = new Bundle();
            mFirebaseAnalytics.logEvent("down_play_click", bundle);
        } catch (Exception e) {
        }
    }

    public static void playuri() {
        try {
            Bundle bundle = new Bundle();
            mFirebaseAnalytics.logEvent("pull_up_player", bundle);
        } catch (Exception e) {
        }
    }


    public static void viaDialogShow() {
        try {
            Bundle bundle = new Bundle();
            mFirebaseAnalytics.logEvent("playDialog", bundle);
        } catch (Exception e) {
        }
    }

    public static void built_in(String way) {
        try {
            Bundle bundle = new Bundle();
            mFirebaseAnalytics.logEvent("clickBulit_" + way, bundle);
        } catch (Exception e) {
        }

    }

    public static void goGp(String way) {
        try {
            Bundle bundle = new Bundle();
            mFirebaseAnalytics.logEvent("gotoGP_" + way, bundle);
        } catch (Exception e) {
        }
    }

    public static void sendRevenue(Bundle bundle) {
        try {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle);
        } catch (Exception e) {

        }
    }
}
