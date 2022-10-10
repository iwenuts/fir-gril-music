package music.mp3.song.app.song.music.tube.ztools;

import android.widget.Toast;

import music.mp3.song.app.song.music.tube.MusicApp;

import me.drakeet.support.toast.ToastCompat;


public class ToastUtils {
    public static void showShortToast(String msg) {
        try {
            ToastCompat.makeText(MusicApp.getInstance(),msg,Toast.LENGTH_SHORT).show();
        } catch (Throwable e){

        }
    }
    public static void showLongToast(String msg) {
        try {
            ToastCompat.makeText(MusicApp.getInstance(),msg,Toast.LENGTH_LONG).show();
        } catch (Throwable e){

        }
    }
    public static void showShortToast(int string) {
        try {
            ToastCompat.makeText(MusicApp.getInstance(), MusicApp.getInstance().getResources().getString(string),Toast.LENGTH_SHORT).show();
        } catch (Throwable e){

        }
    }
    public static void showLongToast(int string) {
        try {
            ToastCompat.makeText(MusicApp.getInstance(), MusicApp.getInstance().getResources().getString(string),Toast.LENGTH_LONG).show();
        } catch (Throwable e){

        }

    }
}
