package music.mp3.song.app.song.music.tube.bean.jm;

import android.text.TextUtils;

import music.mp3.song.app.song.music.tube.bean.BaseBean;
import music.mp3.song.app.song.music.tube.bean.BaseEntity;
import music.mp3.song.app.song.music.tube.firebase.Referrer;

public class JamTrack extends BaseEntity {

    public static final int DOWNLOAD_STATE_NOT_DOWNLOAD = 0;
    public static final int DOWNLOAD_STATE_DOWNLOADING = 1;
    public static final int DOWNLOAD_STATE_DOWNLOADED = 2;

    public int downloadState = DOWNLOAD_STATE_NOT_DOWNLOAD;

    public long id;

    public long artistId;

    public String name;

    public int duration;

    public Stream stream;

    public Cover cover;

    public Stats stats;

    public Status status;

    private AudioInfo audioInfo;

    public String getStreamUrl() {
        return getAudioInfo().url;
    }

    public String getExt() {
        return getAudioInfo().ext;
    }

    private AudioInfo getAudioInfo() {
        if (audioInfo == null) {
            audioInfo = new AudioInfo();
            String ext = null;
            String url = null;
            if (!TextUtils.isEmpty(stream.mp3)) {
                url = stream.mp3;
                ext = ".mp3";
            }
            if (!TextUtils.isEmpty(stream.ogg)) {
                url = stream.ogg;
                ext = ".ogg";
            }
            if (!TextUtils.isEmpty(stream.mp32)) {
                url = stream.mp32;
                ext = ".mp3";
            }
            if (!TextUtils.isEmpty(stream.mp33)) {
                url = stream.mp33;
                ext = ".mp3";
            }

            audioInfo.ext = ext;
            audioInfo.url = url;

        }

        return audioInfo;
    }

    public static class AudioInfo {
        public String url;

        public String ext;
    }

    private String mediaId;

    public String getMediaId() {
        if (mediaId == null) {
            return (mediaId = Referrer.stringToMD5(id + name + duration + artistId));
        }
        return mediaId;
    }

    public static class Stream extends BaseBean {
        public String mp3;
        public String ogg;
        public String mp32;
        public String mp33;

        public boolean isEmpty() {
            return TextUtils.isEmpty(mp3)
                    && TextUtils.isEmpty(ogg)
                    && TextUtils.isEmpty(mp32)
                    && TextUtils.isEmpty(mp33);
        }
    }

    public boolean isUnavailable() {
        return status == null || !status.available || stream == null || stream.isEmpty();
    }

    public String getCover() {
        if (cover == null || cover.small == null) {
            return "";
        }
        Cover.Small small = cover.small;

        if (!TextUtils.isEmpty(small.size130)) {
            return small.size130;
        }
        if (!TextUtils.isEmpty(small.size150)) {
            return small.size150;
        }
        if (!TextUtils.isEmpty(small.size175)) {
            return small.size175;
        }
        if (!TextUtils.isEmpty(small.size200)) {
            return small.size200;
        }
        if (!TextUtils.isEmpty(small.size300)) {
            return small.size300;
        }
        if (!TextUtils.isEmpty(small.size100)) {
            return small.size100;
        }
        if (!TextUtils.isEmpty(small.size600)) {
            return small.size600;
        }
        return "";
    }

    public static class Cover extends BaseBean {

        public Small small;

        public static class Small extends BaseBean {
            public String size100;
            public String size130;
            public String size150;
            public String size175;
            public String size200;
            public String size300;
            public String size600;
        }

    }

    public static class Status extends BaseBean {
        public boolean available;
    }

    public static class Stats extends BaseBean {

        public int downloadedAll;
        public int listenedAll;

    }

}
