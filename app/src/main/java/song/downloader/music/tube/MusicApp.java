package song.downloader.music.tube;

import song.downloader.music.tube.BuildConfig;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;

import com.applovin.sdk.AppLovinSdk;
import com.cyl.musicapi.BaseApiImpl;
import encrypt.pck.JiaMiEncrypted;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;

import song.downloader.music.tube.recommend.RecommendManager;

import song.downloader.music.tube.firebase.FlurryEventReport;
import song.downloader.music.tube.network.freemp3.FreeMp3Cloud;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import song.downloader.music.tube.network.ApiConstants;
import song.downloader.music.tube.network.yt.YTManager;
import song.downloader.music.tube.firebase.InstallReferrer;
import song.downloader.music.tube.firebase.Referrer;
import song.downloader.music.tube.ztools.Config;
import song.downloader.music.tube.ztools.aMathUtils;
import song.downloader.music.tube.ztools.vPrefsUtils;
import song.downloader.music.tube.ztools.Utils;


public class MusicApp extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    public static boolean normalUser = true;
    public static MusicApp mInstance;
    public static Context sContext;
    public static Config config = new Config();
    public static AppLovinSdk appLovinSdk;
    public static int openAbsoluteShow = 0;

    public static MusicApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!getPackageName().equals(getProcessName())) {
                WebView.setDataDirectorySuffix(getProcessName());
            }
        }

        mInstance = this;
        sContext = this;

        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                e.printStackTrace();
            }
        });

        registerActivityLifecycleCallbacks(this);
        YTManager.getInstance().init(this);

        new FlurryAgent.Builder()
                .withDataSaleOptOut(false)
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.VERBOSE)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .build(this, BuildConfig.FLURRY_API_KEY);
        FlurryEventReport.init(this);
        BaseApiImpl.INSTANCE.initWebView(this);
        FreeMp3Cloud.init();
//        enableDebugMaxAd();


        RecommendManager.init(this, aMathUtils.appKey);

        //detect normal user
        int normal = vPrefsUtils.getNormalUser();
        if (normal == vPrefsUtils.INVALID) {
            normal = Utils.isBadUser(this) ? 0 : 1;
            vPrefsUtils.setNormalUser(normal);
        }
        normalUser = (normal > 0);

        //初始化 user
        Referrer.initSuper();

        Config cfg = vPrefsUtils.getConfig();
        postConfig(cfg != null ? cfg : MusicApp.config);

        //handle referer
        InstallReferrer.handleApiReferer();
    }

    private void enableDebugMaxAd() {
        if (BuildConfig.DEBUG) {
            SharedPreferences sp = getSharedPreferences("com.applovin.sdk.preferences."
                    + JiaMiEncrypted.applovin_sdk_key, MODE_PRIVATE);
            sp.edit().putBoolean("com.applovin.sdk.mediation.test_mode_enabled", true).commit();
        }
    }

    public static void postConfig(Config config) {
        MusicApp.config = config;
        try {
            String myVer = mInstance.getPackageManager().getPackageInfo(mInstance.getPackageName(), 0).versionName;
            ApiConstants.onlist = MusicApp.config.onlist.compareTo(myVer) <= 0;
        } catch (Throwable e) {

        }
    }

    private Activity topActivity;
    private int activityCnt = 0;

    public boolean isForeground() {
        return activityCnt > 0;
    }

    public Activity getTopActivity() {
        return topActivity;
    }

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        activityCnt++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        topActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCnt--;
        if (activityCnt <= 0) {
            topActivity = null;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
