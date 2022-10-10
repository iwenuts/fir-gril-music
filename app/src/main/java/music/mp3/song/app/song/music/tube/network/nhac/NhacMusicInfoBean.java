package music.mp3.song.app.song.music.tube.network.nhac;

import java.io.Serializable;

import music.mp3.song.app.song.music.tube.bean.BaseBean;


public class NhacMusicInfoBean extends BaseBean {
    private int errorCode;
    private String errorMsg;
    private DataDTO data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO extends BaseBean implements Serializable {
        private String streaming_url;

        public String getStreaming_url() {
            return streaming_url == null ? "" : streaming_url;
        }

        public void setStreaming_url(String streaming_url) {
            this.streaming_url = streaming_url;
        }
    }
}
