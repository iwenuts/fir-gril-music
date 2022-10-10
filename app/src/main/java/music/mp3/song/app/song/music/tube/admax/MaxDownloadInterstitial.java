package music.mp3.song.app.song.music.tube.admax;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.BuildConfig;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;
import music.mp3.song.app.song.music.tube.ztools.vPrefsUtils;

import java.util.concurrent.TimeUnit;

import static music.mp3.song.app.song.music.tube.MusicApp.appLovinSdk;

public class MaxDownloadInterstitial implements MaxAdListener, MaxAdRevenueListener {

    private static MaxDownloadInterstitial instance = new MaxDownloadInterstitial();

    public static MaxDownloadInterstitial getInstance() {
        return instance;
    }

    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;

    public void loadInterstitialAd(Activity activity) {
        try {
            if (appLovinSdk == null) {
                return;
            }
            if (activity == null) {
                return;
            }
            if (!MusicApp.config.ad) {
                return;
            }
            interstitialAd = new MaxInterstitialAd(BuildConfig.maxDownInterstitialId, appLovinSdk, activity);
            interstitialAd.setListener(this);
            interstitialAd.setRevenueListener(this);
            interstitialAd.loadAd();
        } catch (Exception e) {

        }
    }

    public void destroy() {
        if (!MusicApp.config.ad) {
            return;
        }
        if (interstitialAd != null)
            interstitialAd.destroy();
    }

    public boolean isReady() {
        if (interstitialAd == null) {
            return false;
        }
        if (!MusicApp.config.ad) {
            return false;
        }
        return interstitialAd.isReady();
    }

    public void show() {
        if (MusicApp.config.dl_pop <= 0 || vPrefsUtils.dlNextLong() % MusicApp.config.dl_pop == 3) {
            if (isReady()) {
                interstitialAd.showAd();
            }
        }
    }

    // MAX Ad Listener
    @Override
    public void onAdLoaded(final MaxAd maxAd) {
        retryAttempt = 0;
    }


    @Override
    public void onAdDisplayed(final MaxAd maxAd) {
    }

    @Override
    public void onAdClicked(final MaxAd maxAd) {
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (interstitialAd != null)
                        interstitialAd.loadAd();
                } catch (Exception e) {

                }
            }
        }, delayMillis);
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        try {
            if (interstitialAd != null)
                interstitialAd.loadAd();
        } catch (Exception e) {

        }
    }


    @Override
    public void onAdHidden(final MaxAd maxAd) {
        try {
            if (interstitialAd != null)
                interstitialAd.loadAd();
        } catch (Exception e) {

        }
    }

    @Override
    public void onAdRevenuePaid(MaxAd ad) {
        double revenue = ad.getRevenue(); // In USD
        // Miscellaneous data
        String countryCode = appLovinSdk.getConfiguration().getCountryCode(); // "US" for the United States, etc - Note: Do not confuse this with currency code which is "USD" in most cases!
        String networkName = ad.getNetworkName(); // Display name of the network that showed the ad (e.g. "AdColony")
        String adUnitId = ad.getAdUnitId(); // The MAX Ad Unit ID
        MaxAdFormat adFormat = ad.getFormat(); // The ad format of the ad (e.g. BANNER, MREC, INTERSTITIAL, REWARDED)
        String placement = ad.getPlacement(); // The placement this ad's postbacks are tied to


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "MaxApplovin");
        bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, networkName);
        bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, adFormat.getLabel());
        bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId + "-" + placement);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue);

        FlurryEventReport.sendRevenue(bundle);


    }
}
