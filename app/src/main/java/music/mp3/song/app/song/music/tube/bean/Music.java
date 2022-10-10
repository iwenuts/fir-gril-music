package music.mp3.song.app.song.music.tube.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Arrays;


/**
 *
 */
public class Music extends BaseBean implements Parcelable {
    public static final int CHANNEL_LOCAL = 0;
    public static final int CHANNEL_JAMENDO = 1;
    public static final int CHANNEL_ARCHIVE = 2;
    public static final int CHANNEL_SOUND = 3;
    public static final int CHANNEL_Q = 4;
    public static final int CHANNEL_XM = 5;
    public static final int CHANNEL_BD = 6;
    public static final int CHANNEL_KG = 7;
    public static final int CHANNEL_MJ = 8;
    public static final int CHANNEL_YT = 9;
    public static final int CHANNEL_FREE_MP3 = 11; //
    public static final int CHANNEL_QQ = 12; //
    public static final int CHANNEL_WYY = 13; //
    public static final int CHANNEL_NHAC = 14; //
    public static final int CHANNEL_MP3juice = 15; //

    public int channel;

    public String id;
    public long artistId;
    public String title = "";
    public String artistName = "";
    public String image;
    public String duration;
    public String listenUrl;
    public String downloadUrl;

    //temp
    public String downloadUrl1;
    public String downloadUrl2;
    public String downloadUrl3;
    public int soFarBytes;

    public String getDownloadUrl1() {
        return !TextUtils.isEmpty(downloadUrl1) ? downloadUrl1 : downloadUrl;
    }

    public String getDownloadUrl2() {
        return !TextUtils.isEmpty(downloadUrl2) ? downloadUrl2 : downloadUrl;
    }

    public String getDownloadUrl3() {
        return !TextUtils.isEmpty(downloadUrl3) ? downloadUrl3 : downloadUrl;
    }

    //0:未开始，1正在，2完成,3 error
    public static final int DL_INIT = 0;
    public static final int DL_DOING = 1;
    public static final int DL_DONE = 2;
    public static final int DL_ERROR = 3;
    public static final int DL_CANCEL = 4;
    //add local
    public String location;//本地存储路径
    public String fileName;
    public long realduration;//真实解析的
    public int downloadStats = DL_INIT;
    public int progress;

    public String getTitle() {
        return title;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getImage() {
        return image;
    }

    public String getDuration() {
        return duration;
    }

    public String getListenUrl() {
        return listenUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setListenUrl(String listenUrl) {
        this.listenUrl = listenUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getValidFileName() {
        return getValidFileNameWithoutSuffix() + ".mp3";
    }

    public String getValidCoverName() {
        return getValidFileNameWithoutSuffix() + ".jpg";
    }

    private String getValidFileNameWithoutSuffix() {
        String fileName = Uri.decode(title.replaceAll("-", " ") + "-" + artistName.replaceAll("-", " ") + "-" + id);
        return fileName.replaceAll("[:\\\\/*?\"<>|&#;]", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Music)) return false;
        Music bean = (Music) o;
        return (!TextUtils.isEmpty(downloadUrl) && equals(downloadUrl, bean.downloadUrl))
                || (channel == bean.channel && !TextUtils.isEmpty(id) && id.equals(bean.id)
                || (equals(channel == CHANNEL_LOCAL ? fileName : getValidFileName(), bean.channel == CHANNEL_LOCAL ? bean.fileName : bean.getValidFileName())));
    }

    @Override
    public int hashCode() {
        return hash(downloadUrl);
    }

    private static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.channel);
        dest.writeString(this.id);
        dest.writeLong(this.artistId);
        dest.writeString(this.title);
        dest.writeString(this.artistName);
        dest.writeString(this.image);
        dest.writeString(this.duration);
        dest.writeString(this.listenUrl);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.downloadUrl1);
        dest.writeString(this.downloadUrl2);
        dest.writeString(this.downloadUrl3);
        dest.writeString(this.location);
        dest.writeString(this.fileName);
        dest.writeLong(this.realduration);
        dest.writeInt(this.downloadStats);
        dest.writeInt(this.progress);
    }

    public Music() {
    }

    protected Music(Parcel in) {
        this.channel = in.readInt();
        this.id = in.readString();
        this.artistId = in.readLong();
        this.title = in.readString();
        this.artistName = in.readString();
        this.image = in.readString();
        this.duration = in.readString();
        this.listenUrl = in.readString();
        this.downloadUrl = in.readString();
        this.downloadUrl1 = in.readString();
        this.downloadUrl2 = in.readString();
        this.downloadUrl3 = in.readString();
        this.location = in.readString();
        this.fileName = in.readString();
        this.realduration = in.readLong();
        this.downloadStats = in.readInt();
        this.progress = in.readInt();
    }

    public static final Parcelable.Creator<Music> CREATOR = new Parcelable.Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel source) {
            return new Music(source);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}
