package music.mp3.song.app.song.music.tube.admax;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import music.mp3.song.app.song.music.tube.BuildConfig;
import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;

public
class MaxSearchNative extends MaxNativeAdListener implements MaxAdRevenueListener {
    public MaxSearchNative(Context context) {
        nativeAdLoader = new MaxNativeAdLoader(BuildConfig.maxNativeTemplates, MusicApp.appLovinSdk, context);
        nativeAdLoader.setNativeAdListener(this);
        nativeAdLoader.setRevenueListener(this);
    }

    private MaxNativeAdLoader nativeAdLoader;
    private FrameLayout mNativeAdContainer;
    private MaxAd nativeAd;

    public void createNativeAd(FrameLayout nativeAdContainer) {
        mNativeAdContainer = nativeAdContainer;
        nativeAdLoader.loadAd();
    }


    @Override
    public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
        if (nativeAd != null) {
            nativeAdLoader.destroy(nativeAd);
        }

        nativeAd = ad;
        if (mNativeAdContainer != null) {
            mNativeAdContainer.removeAllViews();
            mNativeAdContainer.addView(nativeAdView);
        }
    }

    @Override
    public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
        System.out.println("------ " + error.toString());
    }

    @Override
    public void onNativeAdClicked(final MaxAd ad) {
        if (nativeAd != null) {
            nativeAdLoader.destroy(nativeAd);
        }
        if (mNativeAdContainer != null)
            mNativeAdContainer.removeAllViews();
    }

    @Override
    public void onAdRevenuePaid(MaxAd ad) {
        double revenue = ad.getRevenue(); // In USD
        // Miscellaneous data
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