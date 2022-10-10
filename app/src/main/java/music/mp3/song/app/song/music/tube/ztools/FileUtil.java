package music.mp3.song.app.song.music.tube.ztools;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Date;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.ui.MyDownloadManager;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class FileUtil {
    public static Uri copyUri(File from, String fileName) {
        return copyUri(from, fileName, null);
    }

    public static Uri copyUri(File from, String fileName, String parentName) {
        try {
            if (TextUtils.isEmpty(parentName)) {
                parentName = MyDownloadManager.folder;
            }
            ContentResolver resolver = MusicApp.getInstance().getContentResolver();
            Uri audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            ContentValues newSongDetails = new ContentValues();
            newSongDetails.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
            newSongDetails.put(MediaStore.MediaColumns.RELATIVE_PATH, "Music/" + parentName);
            Uri songContentUri = resolver.insert(audioCollection, newSongDetails);
            if (songContentUri == null) {
                newSongDetails.clear();
                newSongDetails.put(MediaStore.Audio.Media.DISPLAY_NAME, "new_" +(new Date().toString()) + fileName);
                newSongDetails.put(MediaStore.MediaColumns.RELATIVE_PATH, "Music/" + parentName);
                songContentUri = resolver.insert(audioCollection, newSongDetails);
            }
            BufferedSource source = Okio.buffer(Okio.source(from));
            ContentResolver contentResolver = MusicApp.getInstance().getContentResolver();
            ParcelFileDescriptor file = contentResolver.openFileDescriptor(songContentUri, "rw");
            FileOutputStream out = new FileOutputStream(file.getFileDescriptor());
            BufferedSink sink = Okio.buffer(Okio.sink(out));

            sink.writeAll(source);
            sink.close();
            source.close();
            from.delete();
//            EventBus.getDefault().post(new DownloadSuccessEvent());
            return songContentUri;
        } catch (Throwable e) {
            return null;
        }
    }


    public static String getSizeDescription(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }


}
