package music.mp3.song.app.song.music.tube.referrer;

import android.os.Parcel;

public class ReferrerItem extends BaseReferrer {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public ReferrerItem() {
    }

    protected ReferrerItem(Parcel in) {
        super(in);
    }

    public static final Creator<ReferrerItem> CREATOR = new Creator<ReferrerItem>() {
        @Override
        public ReferrerItem createFromParcel(Parcel source) {
            return new ReferrerItem(source);
        }

        @Override
        public ReferrerItem[] newArray(int size) {
            return new ReferrerItem[size];
        }
    };
}
