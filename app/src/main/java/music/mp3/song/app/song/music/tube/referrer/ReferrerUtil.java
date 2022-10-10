package music.mp3.song.app.song.music.tube.referrer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.afollestad.materialdialogs.MaterialDialog; 
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import music.mp3.song.app.song.music.tube.MusicApp;

public class ReferrerUtil {

    public static void openDir(String pkg, String dir) {
        MusicApp context = MusicApp.getInstance();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(pkg);

        Uri uri = new Uri.Builder()
                .scheme("directory") // scheme
                .authority("external") // todo external for legacy external storage, media for scoped storage
                .path("audio/" + dir) // media format: audio, video, picture, path
                .build();

        intent.setData(uri);
        try {
            context.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @return true successful open, false otherwise
     */
    public static boolean playMusic(String pkg, String path) {
        MusicApp context = MusicApp.getInstance();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(pkg);

        Uri uri = new Uri.Builder()
                .scheme("playlist") // scheme
                .authority("play") // path
                .path("0") // play index
                .build();

        ArrayList<String> playlist = new ArrayList<>();
        playlist.add(path);
        intent.putStringArrayListExtra("playlist", playlist);
        intent.setData(uri);
        if (openIntent(context, intent)) {
            return true;
        }
        intent.removeExtra("playlist");
        intent.setData(null);

        try {
            uri = MediaStore.Audio.Media.getContentUriForPath(path);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (uri == null) {
            try {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(path));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        if (uri == null) {
            return false;
        }
        intent.setDataAndType(uri, "audio/*");

        if (openIntent(context, intent)) {
            return true;
        }
        intent.setData(uri);
        return openIntent(context, intent);
    }

    private static boolean openIntent(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean listItemShowDialog(int times) {
        SharedPreferences sp = MusicApp.getInstance().getSharedPreferences("list_item_time", Context.MODE_PRIVATE);
        int time = sp.getInt("time", 0);
        sp.edit().putInt("time", time + 1).apply();
        return time < times;
    }

    public static void preloadImage() {
        ReferrerStream referrer = MusicApp.config.getUseReferrer();
        if (referrer == null) {
            return;
        }
        List<String> images = referrer.getAllImage();
        for (String image : images) {
            if (!TextUtils.isEmpty(image)) {
                Picasso.get().load(image).fetch();
            }
        }
    }

    public interface ActionCallback {
        void onActionClick(MaterialDialog dialog);
    }

//    public static void showDialog(Context context, int layout, BaseReferrer referrer, ActionCallback c) {
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
//        MaterialDialog dialog = builder.customView(layout, false)
//                .canceledOnTouchOutside(false)
//                .build();
//        ImageView posterIv = (ImageView) dialog.findViewById(R.id.poster_iv);
//        ImageView iconIv = (ImageView) dialog.findViewById(R.id.icon_iv);
//        TextView titleTv = (TextView) dialog.findViewById(R.id.title_tv);
//        TextView subtitleTv = (TextView) dialog.findViewById(R.id.subtitle_tv);
//        TextView actionBtn = (TextView) dialog.findViewById(R.id.action_btn);
//
//        if (posterIv != null) {
//            if (!TextUtils.isEmpty(referrer.poster)) {
//                Picasso.get().load(referrer.poster).into(posterIv);
//            }
//        }
//        if (iconIv != null) {
//            if (!TextUtils.isEmpty(referrer.icon)) {
//                Picasso.get().load(referrer.icon).into(iconIv);
//            }
//        }
//        if (titleTv != null) {
//            if (!TextUtils.isEmpty(referrer.title)) {
//                titleTv.setText(referrer.title);
//            }
//        }
//
//        if (subtitleTv != null) {
//            if (!TextUtils.isEmpty(referrer.subtitle)) {
//                subtitleTv.setText(referrer.subtitle);
//            }
//        }
//        if (actionBtn != null) {
//            if (!TextUtils.isEmpty(referrer.action)) {
//                actionBtn.setText(referrer.action);
//            }
//            actionBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (c != null) {
//                        c.onActionClick(dialog);
//                    }
//                }
//            });
//        }
//
//        View close = dialog.findViewById(R.id.close_iv);
//        if (close != null) {
//            close.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//        }
//        dialog.show();
//    }

}
