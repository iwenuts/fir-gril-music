package music.mp3.song.app.song.music.tube.bean.jm;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import music.mp3.song.app.song.music.tube.bean.BaseBean;

public class JamTag extends BaseBean {

    public long id;

    public String name;

    public String lang;

    public String idstr;

    public int featuredRank;

    public Cover cover;

    public static class Cover extends BaseBean {

        @SerializedName("tile-xs")
        public String tileXs;

        @SerializedName("tile-sm")
        public String tileSm;

        public String getCover() {
            if (TextUtils.isEmpty(tileSm)) {
                return TextUtils.isEmpty(tileXs) ? "" : tileXs;
            }
            return tileSm;
        }
    }
}
