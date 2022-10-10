package music.mp3.song.app.song.music.tube.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

public class Mp3JuiceMusicDetail {

    /**
     * error : false
     * vidID : qdDVtFvJwUc
     * vidTitle : Justin Bieber - Love Me (Official Music Video)
     * vidThumb : https://img.youtube.com/vi/qdDVtFvJwUc/0.jpg
     * duration : 193
     * vidInfo : [{"ftype":"MP3","dloadUrl":"https://cdn02.mp3yt.link/download/qdDVtFvJwUc/mp3/320/1644826485/59b115229e22f23a224117f622c1aaae12b741c04cea954f33008d27b85cd0f2/1","bitrate":320,"mp3size":"7.36 MB"},{"ftype":"MP3","dloadUrl":"https://cdn02.mp3yt.link/download/qdDVtFvJwUc/mp3/256/1644826485/59b115229e22f23a224117f622c1aaae12b741c04cea954f33008d27b85cd0f2/1","bitrate":256,"mp3size":"5.89 MB"},{"ftype":"MP3","dloadUrl":"https://cdn02.mp3yt.link/download/qdDVtFvJwUc/mp3/192/1644826485/59b115229e22f23a224117f622c1aaae12b741c04cea954f33008d27b85cd0f2/1","bitrate":192,"mp3size":"4.42 MB"},{"ftype":"MP3","dloadUrl":"https://cdn02.mp3yt.link/download/qdDVtFvJwUc/mp3/128/1644826485/59b115229e22f23a224117f622c1aaae12b741c04cea954f33008d27b85cd0f2/1","bitrate":128,"mp3size":"2.94 MB"},{"ftype":"MP3","dloadUrl":"https://cdn02.mp3yt.link/download/qdDVtFvJwUc/mp3/64/1644826485/59b115229e22f23a224117f622c1aaae12b741c04cea954f33008d27b85cd0f2/1","bitrate":64,"mp3size":"1.47 MB"}]
     */

    private boolean error;
    private String vidID;
    private String vidTitle;
    private String vidThumb;
    private String duration;
    private List<VidInfoBean> vidInfo;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getVidID() {
        return vidID;
    }

    public void setVidID(String vidID) {
        this.vidID = vidID;
    }

    public String getVidTitle() {
        return vidTitle;
    }

    public void setVidTitle(String vidTitle) {
        this.vidTitle = vidTitle;
    }

    public String getVidThumb() {
        return vidThumb;
    }

    public void setVidThumb(String vidThumb) {
        this.vidThumb = vidThumb;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<VidInfoBean> getVidInfo() {
        return vidInfo;
    }

    public void setVidInfo(List<VidInfoBean> vidInfo) {
        this.vidInfo = vidInfo;
    }

    public static class VidInfoBean implements Serializable {
        /**
         * ftype : MP3
         * dloadUrl : https://cdn02.mp3yt.link/download/qdDVtFvJwUc/mp3/320/1644826485/59b115229e22f23a224117f622c1aaae12b741c04cea954f33008d27b85cd0f2/1
         * bitrate : 320
         * mp3size : 7.36 MB
         */

        private String ftype;
        private String dloadUrl;
        private int bitrate;
        private String mp3size;

        public String getFtype() {
            return ftype;
        }

        public void setFtype(String ftype) {
            this.ftype = ftype;
        }

        public String getDloadUrl() {
            return dloadUrl;
        }

        public void setDloadUrl(String dloadUrl) {
            this.dloadUrl = dloadUrl;
        }

        public int getBitrate() {
            return bitrate;
        }

        public void setBitrate(int bitrate) {
            this.bitrate = bitrate;
        }

        public String getMp3size() {
            return mp3size;
        }

        public void setMp3size(String mp3size) {
            this.mp3size = mp3size;
        }
    }
    public boolean isSuccessful(){
        return false == error;
    }
}
