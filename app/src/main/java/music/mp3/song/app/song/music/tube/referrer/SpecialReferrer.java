package music.mp3.song.app.song.music.tube.referrer;

import android.os.Parcel;

public class SpecialReferrer extends BaseReferrer {

    public int times = 4;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.times);
    }

    public SpecialReferrer() {
    }

    protected SpecialReferrer(Parcel in) {
        super(in);
        this.times = in.readInt();
    }

    public static final Creator<SpecialReferrer> CREATOR = new Creator<SpecialReferrer>() {
        @Override
        public SpecialReferrer createFromParcel(Parcel source) {
            return new SpecialReferrer(source);
        }

        @Override
        public SpecialReferrer[] newArray(int size) {
            return new SpecialReferrer[size];
        }
    };
}
