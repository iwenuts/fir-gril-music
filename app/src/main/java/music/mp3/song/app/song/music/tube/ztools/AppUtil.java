package music.mp3.song.app.song.music.tube.ztools;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import music.mp3.song.app.song.music.tube.MusicApp;

public class AppUtil {


    public static boolean appInstalled(String pkg) {
        MusicApp context = MusicApp.getInstance();
        PackageManager pm = context.getPackageManager();
        try {
            pm.getApplicationInfo(pkg, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }



    public static void openApp(String pkg) {
        MusicApp context = MusicApp.getInstance();
        PackageManager pm = context.getPackageManager();
        try {
            Intent intent = pm.getLaunchIntentForPackage(pkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void openGP(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return;
        }
        MusicApp context = MusicApp.getInstance();
        try {
            Intent launchIntent = new Intent(Intent.ACTION_VIEW);
            launchIntent.setPackage("com.android.vending");
            launchIntent.setData(Uri.parse("market://details?id=" + pkg));
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        } catch (Throwable e) {
            e.printStackTrace();
            try {
                Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + pkg));
                context.startActivity(launchIntent);
            } catch (Throwable ee) {
                ee.printStackTrace();
            }
        }
    }

}
