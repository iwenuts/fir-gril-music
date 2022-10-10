package music.mp3.song.app.song.music.tube.recommend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;

/* compiled from: RecommendUtils */
public class RecommendUtils {
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences("recommend", 0);
    }

    public static int getMaxShowCount() {
        return sharedPreferences.getInt("showcount", 3);
    }

    public static int dp2px(Context context, float f) {
        return (int) ((context.getResources().getDisplayMetrics().density * f) + 0.5f);
    }

    public static void setCount(String str, int i) {
        sharedPreferences.edit().putInt(str + "count", i).apply();
    }

    public static int getCount(String str) {
        return sharedPreferences.getInt(str + "count", 0);
    }

    public static boolean getCanClick(String str) {
        return sharedPreferences.getBoolean(str + "click", true);
    }

    public static void setCanClick(String str) {
        sharedPreferences.edit().putBoolean(str + "click", false).apply();
    }

    public static Animation rotateIcon(int i) {
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 20.0f, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setInterpolator(new CycleInterpolator((float) i));
        rotateAnimation.setDuration(4000);
        return rotateAnimation;
    }

    public static boolean isInstalled(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            //查询已经安装的应用程序
            context.getPackageManager().getApplicationInfo(str, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    public static void gotoGP(String str) {
        try {
            setCanClick(str);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("market://details?id=" + str));
            intent.setPackage("com.android.vending");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(RecommendManager.context.getPackageManager()) != null) {
                RecommendManager.context.startActivity(intent);
                return;
            }
            intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + str));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(RecommendManager.context.getPackageManager()) != null) {
                RecommendManager.context.startActivity(intent);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
