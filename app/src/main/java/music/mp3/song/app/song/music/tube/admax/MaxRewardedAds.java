package music.mp3.song.app.song.music.tube.admax;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import music.mp3.song.app.song.music.tube.BuildConfig;
import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;
import music.mp3.song.app.song.music.tube.ztools.AdRewardedDialog;

import java.util.concurrent.TimeUnit;

import static music.mp3.song.app.song.music.tube.MusicApp.appLovinSdk;

public class MaxRewardedAds implements MaxRewardedAdListener, MaxAdRevenueListener {
    private MaxRewardedAd rewardedAd;
    private int retryAttempt;

    private static MaxRewardedAds instance = new MaxRewardedAds();
    private MaxRewardLoadedInterface mLoadedInterface;

    public static MaxRewardedAds getInstance() {
        return instance;
    }

    public void createRewardedAd(Activity activity) {
        if (activity == null) {
            return;
        }
        if (appLovinSdk == null) {
            return;
        }
        if (!MusicApp.config.ad) {
            return;
        }
        try {
            rewardedAd = MaxRewardedAd.getInstance(BuildConfig.maxDownRewardId, appLovinSdk, activity);
            rewardedAd.setListener(this);
            rewardedAd.setRevenueListener(this);
            rewardedAd.loadAd();
        } catch (Exception e) {

        }
    }

    public void loadAd() {
        if (rewardedAd == null) {
            return;
        }
        if (!MusicApp.config.ad) {
            return;
        }
        rewardedAd.loadAd();
    }


    public void tryShowReward(final Activity context, Music bean, AdRewardedDialog.OnRewardCallback callback) {
        new AdRewardedDialog(this, callback).show(context, bean);
    }


    public void show() {
        if (isReady()) {
            rewardedAd.showAd();
        }
    }

    public boolean isReady() {
        if (rewardedAd == null) {
            return false;
        }
        if (!MusicApp.config.ad) {
            return false;
        }
        return rewardedAd.isReady();
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

    public interface MaxRewardLoadedInterface {
        void onAdLoaded();

        void onReward();

        void onAdClosed();

        void onError();
    }

    public void setMaxRewardLoadedInterface(MaxRewardLoadedInterface loadedInterface) {
        mLoadedInterface = loadedInterface;
    }


    // MAX Ad Listener
    @Override
    public void onAdLoaded(final MaxAd maxAd) {
        retryAttempt = 0;
//        EventBus.getDefault().post(new RewardLoadedEvent());
        if (mLoadedInterface != null) {
            mLoadedInterface.onAdLoaded();
        }
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) {
        System.out.println("-----------  onAdDisplayed");
    }

    @Override
    public void onAdClicked(final MaxAd maxAd) {
        System.out.println("-----------  onAdDisplayed");
    }


    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    rewardedAd.loadAd();
                } catch (Exception e) {

                }
            }
        }, delayMillis);
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        try {
            rewardedAd.loadAd();
        } catch (Exception e) {

        }
        if (mLoadedInterface != null) {
            mLoadedInterface.onError();
        }
    }

    @Override
    public void onAdHidden(final MaxAd maxAd) {
        // rewarded ad is hidden. Pre-load the next ad
        System.out.println("-----------  onAdHidden");
//        rewardedAd.loadAd();
        if (mLoadedInterface != null) {
            mLoadedInterface.onAdClosed();
        }
    }

    @Override
    public void onRewardedVideoStarted(final MaxAd maxAd) {
        System.out.println("-----------  onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoCompleted(final MaxAd maxAd) {
        System.out.println("-----------  onRewardedVideoCompleted");
//        EventBus.getDefault().post(new RewardLoadedEvent());
    }

    @Override
    public void onUserRewarded(final MaxAd maxAd, final MaxReward maxReward) {
        // Rewarded ad was displayed and user should receive the reward
        System.out.println("-----------  onUserRewarded");
//        EventBus.getDefault().post(new RewardEvent());
        if (mLoadedInterface != null) {
            mLoadedInterface.onReward();
        }
    }
}
