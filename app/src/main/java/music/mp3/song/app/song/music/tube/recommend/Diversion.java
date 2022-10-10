package music.mp3.song.app.song.music.tube.recommend;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;
import music.mp3.song.app.song.music.tube.player.APlayer;
import music.mp3.song.app.song.music.tube.referrer.ReferrerStream;
import music.mp3.song.app.song.music.tube.ui.PlayMusicViaDialog;
import music.mp3.song.app.song.music.tube.ztools.AppUtil;
import music.mp3.song.app.song.music.tube.ztools.ViaDialogCountUtil;

public class Diversion {

    public static void launchPlayer(Context context, ArrayList<Music> arrayList, int index, String way) {
        Music bean = arrayList.get(index);
        if (bean.downloadStats != Music.DL_DONE) {
            launch(arrayList, index);
            return;
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            launch(arrayList, index);
//            return;
//        }

        ReferrerStream referrerStream = MusicApp.config.referrer;
//        referrerStream = new ReferrerStream();
//        referrerStream.player_feature = new SpecialReferrer();
//        referrerStream.player_feature.title= "title";
//        referrerStream.player_feature.icon= "title";
//        referrerStream.player_feature.pkg= "music.location.music.app";

        if (referrerStream == null || referrerStream.player_feature == null) {
            launch(arrayList, index);
            return;
        }
        String pkg = referrerStream.player_feature.pkg;
        if (AppUtil.appInstalled(pkg)) {
            String location = bean.location;
            if (!location.startsWith("content://")) {
                File file = new File(bean.location, bean.fileName);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
                    if (uri != null) {
                        Pattern pattern = Pattern.compile("\\d+$");
                        Matcher matcher = pattern.matcher(uri.toString());
                        if (matcher.find()) {
                            toPlayer(pkg, uri.toString());
                            return;
                        }
                    }
                    uri = FileProvider.getUriForFile(MusicApp.getInstance(), MusicApp.getInstance().getPackageName() + ".provider", file);
                    if (uri != null) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setPackage(pkg);
                            intent.setDataAndType(uri, "audio/*");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            context.startActivity(intent);
                            if (openIntent(context, intent)) {
                                return;
                            }
//                            FlurryEventReport.playlocation();
                        } catch (Exception e) {
                            launch(arrayList, index);
                        }
                        return;
                    }
                    launch(arrayList, index);
                    return;
                } else {
                    location = file.getAbsolutePath();
                    toPlayer(pkg, location);
                    return;
                }
            }
            toPlayer(pkg, location);
        } else {
            if (TextUtils.equals(way, "way1")) {
                boolean isShowDialog = ViaDialogCountUtil.canShowDialog();
                if (isShowDialog) {
                    PlayMusicViaDialog viaDialog = new PlayMusicViaDialog(context, arrayList, index);
                    viaDialog.show();
                } else {
                    launch(arrayList, index);
                }
            } else {
                boolean isGoGp = ViaDialogCountUtil.canShowDialog();
                if (isGoGp) {
                    ViaDialogCountUtil.reduceCount();
                    String toGpPkg = referrerStream.player_feature.getPkg(true);
                    RecommendUtils.gotoGP(toGpPkg);
                    FlurryEventReport.goGp("way2");
                }else {
                    launch(arrayList, index);
                }
            }
        }
    }

    public static void launch(ArrayList<Music> arrayList, int index) {
        try {
            APlayer.playList(arrayList, index);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean toPlayer(String pkg, String musicUri) {
        MusicApp context = MusicApp.getInstance();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.setPackage(pkg);
        Uri uri = new Uri.Builder()
                .scheme("playlist") // scheme
                .authority("play") // path
                .path("0") // play index
                .build();

        ArrayList<String> playlist = new ArrayList<>();
        playlist.add(musicUri);
        intent.putStringArrayListExtra("playlist", playlist);
        intent.setData(uri);
        if (openIntent(context, intent)) {
            return true;
        }
        return false;
    }

    private static boolean openIntent(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            FlurryEventReport.playuri();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

}
