package song.downloader.music.tube.ztools;

import android.util.Log;

import song.downloader.music.tube.BuildConfig;

public class LogUtil {

    public static void v(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }
}
