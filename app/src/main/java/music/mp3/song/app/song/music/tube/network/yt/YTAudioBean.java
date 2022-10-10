package music.mp3.song.app.song.music.tube.network.yt;

import java.io.Serializable;

public class YTAudioBean implements Serializable {
    public String url;
    public String quality;
    public String size;
    public String format;

    @Override
    public String toString() {
        return "YTAudioBean{" +
                "url='" + url + '\'' +
                ", quality='" + quality + '\'' +
                ", size='" + size + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
