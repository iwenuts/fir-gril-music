package music.mp3.song.app.song.music.tube.bean;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by liyanju on 2018/5/7.
 */

public class MusicSuggistion implements SearchSuggestion {

    private String suggistion;

    public MusicSuggistion(String suggistion) {
        this.suggistion = suggistion;
    }

    public MusicSuggistion(Parcel in) {
        suggistion = in.readString();
    }

    @Override
    public String getBody() {
        return suggistion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(suggistion);
    }

    public static final Creator<MusicSuggistion> CREATOR = new Creator<MusicSuggistion>() {
        @Override
        public MusicSuggistion createFromParcel(Parcel source) {
            return new MusicSuggistion(source);
        }

        @Override
        public MusicSuggistion[] newArray(int size) {
            return new MusicSuggistion[size];
        }
    };
}
