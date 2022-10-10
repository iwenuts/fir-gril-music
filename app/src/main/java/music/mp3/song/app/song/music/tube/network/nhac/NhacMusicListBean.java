package music.mp3.song.app.song.music.tube.network.nhac;

import java.util.ArrayList;
import java.util.List;

import music.mp3.song.app.song.music.tube.bean.BaseBean;


public class NhacMusicListBean extends BaseBean {
    private int errorCode;
    private String errorMsg;
    private int total;
    private java.util.List<DataBean> data;

    public static class DataBean extends BaseBean {
        private String _type;
        private int id;
        private String title;
        private String artist_id;
        private String image_url;
        private String image_url_large;
        private String artist_title;

        public String get_type() {
            return _type;
        }

        public void set_type(String _type) {
            this._type = _type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtist_id() {
            return artist_id;
        }

        public void setArtist_id(String artist_id) {
            this.artist_id = artist_id;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getImage_url_large() {
            return image_url_large;
        }

        public void setImage_url_large(String image_url_large) {
            this.image_url_large = image_url_large;
        }

        public String getArtist_title() {
            return artist_title;
        }

        public void setArtist_title(String artist_title) {
            this.artist_title = artist_title;
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg == null ? "" : errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        if (data == null) {
            return new ArrayList<>();
        }
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }
}
