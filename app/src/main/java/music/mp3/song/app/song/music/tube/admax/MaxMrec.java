package music.mp3.song.app.song.music.tube.admax;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import music.mp3.song.app.song.music.tube.BuildConfig;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;

import static music.mp3.song.app.song.music.tube.MusicApp.appLovinSdk;

public class MaxMrec implements MaxAdViewAdListener, MaxAdRevenueListener {

    private static MaxMrec mMaxMrecs = new MaxMrec();

    private MaxMrec() {

    }

    public static MaxMrec getInstance() {
        return mMaxMrecs;
    }

    private MaxAdView adView;
    private ViewGroup mRootView;

    public void createMrecAd(Activity activity, ViewGroup rootView) {
        if (activity == null) {
            return;
        }
        if (rootView == null) {
            return;
        }
        if (appLovinSdk == null) {
            return;
        }
        Context context = activity;
        adView = new MaxAdView(BuildConfig.maxMrecId, MaxAdFormat.MREC,appLovinSdk, activity);
        adView.setListener(this);
        int heightPx = AppLovinSdkUtils.dpToPx(context, 250);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;


        adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));

        // Set background or background color for MRECs to be fully functional
//        adView.setBackgroundColor(R.color.background_color);
        mRootView = rootView;
        rootView.removeAllViews();
        rootView.addView(adView);


        try {
            adView.setRevenueListener(this);
            adView.loadAd();
        } catch (Exception e) {

        }

    }


    public void startAutoRefresh() {
        if (adView == null) {
            return;
        }
        if (mRootView == null) {
            return;
        }

        mRootView.setVisibility(View.VISIBLE);
        adView.setVisibility(View.VISIBLE);
        adView.startAutoRefresh();
    }

    public void stopAutoRefresh() {
        if (adView == null) {
            return;
        }
        if (mRootView == null) {
            return;
        }
        mRootView.setVisibility(View.GONE);
        adView.setVisibility(View.GONE);
        adView.stopAutoRefresh();
    }


    // MAX Ad Listener
    @Override
    public void onAdLoaded(final MaxAd maxAd) {
        startAutoRefresh();
    }

    @Override
    public void onAdClicked(final MaxAd maxAd) {
        stopAutoRefresh();
    }


    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {

    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {

    }

    @Override
    public void onAdExpanded(final MaxAd maxAd) {
    }

    @Override
    public void onAdCollapsed(final MaxAd maxAd) {
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) { /* DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE */ }

    @Override
    public void onAdHidden(final MaxAd maxAd) { /* DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE */ }

    public void destroy() {
        if (adView != null) {
            adView.destroy();
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
