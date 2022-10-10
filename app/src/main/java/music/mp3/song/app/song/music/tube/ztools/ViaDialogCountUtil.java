package music.mp3.song.app.song.music.tube.ztools;

import android.content.Context;
import android.content.SharedPreferences;

import music.mp3.song.app.song.music.tube.MusicApp;

public class ViaDialogCountUtil {

    private static SharedPreferences getSp() {
        return MusicApp.getInstance().getSharedPreferences("show_count_u", Context.MODE_PRIVATE);
    }

    public static int availableCount() {
        return getSp().getInt("avai_show", 3);
    }

    public static void reduceCount() {
        int c = availableCount();
        c--;
        getSp().edit().putInt("avai_show", c).apply();
    }

//    public static void resetCount() {
//        getSp().edit().putInt("avai_show", MyApp.config.playerinterval).apply();
//    }

    public static boolean canShowDialog() {
        boolean shown = getSp().getBoolean("via_dialog_show", false);
        if (shown) {
            return false;
        }
        int c = availableCount();
        if (c >= 1) {
            return true;
        }
        getSp().edit().putBoolean("via_dialog_show", true).apply();
        return false;
    }
}
