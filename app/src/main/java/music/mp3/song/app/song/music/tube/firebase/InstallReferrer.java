package music.mp3.song.app.song.music.tube.firebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.ztools.vPrefsUtils;

import java.net.URLDecoder;


/**
 *
 */

public class InstallReferrer extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String referrer = intent.getStringExtra("referrer");
            String from = "broadcast";

            boolean result = vPrefsUtils.getRefererDone();
            if (result) {
                return;
            }
            vPrefsUtils.setReferDone(true);

            doHandle(context, referrer, from);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void handleApiReferer() {
        //启动后只调用一次
        if (vPrefsUtils.getRefererApiDone()) {
            return;
        }
        vPrefsUtils.setReferApiDone(true);

        //统计国家代码
        FlurryEventReport.logSentUserInfo(Referrer.getSimCountry(MusicApp.getInstance().getApplicationContext()),
                Referrer.getPhoneCountry(MusicApp.getInstance().getApplicationContext()));


        InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(MusicApp.getInstance().getApplicationContext()).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                String from = "api";
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established
                        try {
                            ReferrerDetails response = referrerClient.getInstallReferrer();
                            String referrer = response.getInstallReferrer();
//                            long clicktime = response.getReferrerClickTimestampSeconds();
//                            long begintime = response.getInstallBeginTimestampSeconds();
//                            boolean gpi = response.getGooglePlayInstantParam();
                            doHandle(MusicApp.getInstance().getApplicationContext(), referrer, from);

                        } catch (Throwable e) {
                            FlurryEventReport.logSentReferFalse("ref_exception", from);
                            e.printStackTrace();
                        }

                        referrerClient.endConnection();
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app
                        FlurryEventReport.logSentReferFalse("ref_not_supported", from);
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection could not be established
                        FlurryEventReport.logSentReferFalse("ref_srv_unavailable", from);
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });

    }

    public static void doHandle(Context context, String referrer, String from) {
        if (TextUtils.isEmpty(referrer)) {
            FlurryEventReport.logSentReferFalse("refer_empty", from);
            return;
        }

        try {
            referrer = URLDecoder.decode(referrer, "utf-8");
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }

        if (!MusicApp.normalUser) {
            FlurryEventReport.logSentReferFalse("ref_fake", from);
            return;
        }
        FlurryEventReport.logSentReferrer(referrer, from);

        if (Referrer.isAdmobOpen(referrer)) {
            Referrer.setSuper();
            FlurryEventReport.logSentOpenSuper("ref_admob", from);
        } else if (Referrer.isFacebookOpen(referrer)) {
            Referrer.setSuper();
            FlurryEventReport.logSentOpenSuper("ref_recom", from);
        }

    }
}
