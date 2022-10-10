package music.mp3.song.app.song.music.tube.ztools;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.provider.Settings;

import com.google.gson.Gson;
import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.BuildConfig;
import music.mp3.song.app.song.music.tube.ui.MyDownloadManager;
import music.mp3.song.app.song.music.tube.bean.Music;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okio.BufferedSource;
import okio.Okio;

import static music.mp3.song.app.song.music.tube.bean.Music.CHANNEL_LOCAL;
import static music.mp3.song.app.song.music.tube.bean.Music.DL_DONE;

public class Utils {

    public static final ExecutorService sExecutorService = Executors.newSingleThreadExecutor();

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static List<Music> scanDir(String dir) {
        List<Music> musicList = new ArrayList<>();
        File file = new File(dir);
        if (file.exists() && file.isDirectory()) {
            File[] flist = file.listFiles();
            if (flist == null || flist.length <= 0) {
                return musicList;
            }
            List<File> arrayList = new ArrayList<>(Arrays.asList(flist));
            for (Iterator<File> it = arrayList.iterator(); it.hasNext(); ) {
                File f = it.next();
                if (!f.isFile() || f.getName().length() <= 4 || !f.getName().toLowerCase().endsWith(".mp3")) {
                    it.remove();
                }
            }
            for (File musicFile : sort(arrayList.toArray(new File[arrayList.size()]))) {
                Music music = new Music();
                music.downloadStats = DL_DONE;
                music.channel = CHANNEL_LOCAL;
                music.location = file.getAbsolutePath();
                music.fileName = musicFile.getName();
                String title = music.fileName.substring(0, music.fileName.length() - 4); //remove .mp3
                String[] songInfo = title.split("-");
                if (songInfo.length == 3) {
                    //假设为 title + "-" + artistName + "-" + id
                    music.title = songInfo[0];
                    music.artistName = songInfo[1];
                    music.id = songInfo[2];
                } else {
                    music.title = title;
                    music.artistName = "";
                    music.id = "";
                }

                //封面
                File cover = new File(MyDownloadManager.getInstance().getCoverPath(), title + ".jpg");
                if (cover.exists()) {
                    music.image = cover.getAbsolutePath();
                }
                musicList.add(music);
            }
        }
        return musicList;
    }

    private static File[] sort(File[] fileArr) {
        Arrays.sort(fileArr, new Comparator<File>() {
            public int compare(File file, File file2) {
                long lastModified = file.lastModified() - file2.lastModified();
                if (lastModified > 0) {
                    return 1;
                }
                return lastModified == 0 ? 0 : -1;
            }
        });
        return fileArr;
    }

    /**
     * @return true 开启调试，false 未开启调试
     * @author James
     * @Description 是否是usb调试模式
     */
    @TargetApi(3)
    private static boolean isAdbDebugEnable(Context mContext) {
        boolean enableAdb = (Settings.Secure.getInt(
                mContext.getContentResolver(), Settings.Global.ADB_ENABLED, 0) > 0);
        return enableAdb;
    }

    private static boolean isEmulator() {
        try {
            return Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.toLowerCase().contains("vbox")
                    || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                    || "google_sdk".equals(Build.PRODUCT);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 不是普通用户
     * 设备开启Debug模式，模拟器，以及ROOT的手机都认为不是普通玩家
     *
     * @return
     */
    public static boolean isBadUser(Context context) {
        if (BuildConfig.DEBUG) {
            return false;
        }
        return isAdbDebugEnable(context) || isEmulator() || isRoot();
    }

    private static boolean isRoot() {
        boolean bool = false;

        try {
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())) {
                bool = false;
            } else {
                bool = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bool;
    }

    public static String getlocaleCountry(){
        return MusicApp.mInstance.getResources().getConfiguration().locale.getCountry();
    }

    public static String getLocaleLanguage(){
        return MusicApp.mInstance.getResources().getConfiguration().locale.getLanguage();
    }

//    public String getlanguage(){
//        return Locale.getDefault().getLanguage();
//    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }


    private static Gson gson;

    private static void ensureGson() {
        if (gson == null) {
            gson = new Gson();
        }
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        ensureGson();
        return gson.fromJson(json, typeOfT);
    }

    public static String readAsset(String name) {
        InputStream is = null;
        BufferedSource bs = null;
        try {
            AssetManager assets = MusicApp.getInstance().getAssets();
            is = assets.open(name);
            bs = Okio.buffer(Okio.source(is));
            return bs.readUtf8();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            safeClose(is);
            safeClose(bs);
        }
    }

    public static void safeClose(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
