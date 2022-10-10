package music.mp3.song.app.song.music.tube.ztools;

import android.util.Log;

import music.mp3.song.app.song.music.tube.BuildConfig;

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
