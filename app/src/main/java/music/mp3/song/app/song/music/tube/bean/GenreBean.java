package music.mp3.song.app.song.music.tube.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class GenreBean extends BaseBean implements Parcelable {
    public int id;
    public String title;
    public String image;

    public int followers;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.image);
        dest.writeInt(this.followers);
    }

    public GenreBean() {
    }

    protected GenreBean(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.image = in.readString();
        this.followers = in.readInt();
    }

    public static final Parcelable.Creator<GenreBean> CREATOR = new Parcelable.Creator<GenreBean>() {
        @Override
        public GenreBean createFromParcel(Parcel source) {
            return new GenreBean(source);
        }

        @Override
        public GenreBean[] newArray(int size) {
            return new GenreBean[size];
        }
    };
}
