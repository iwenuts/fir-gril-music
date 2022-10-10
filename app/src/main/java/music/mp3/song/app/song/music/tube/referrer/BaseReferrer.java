package music.mp3.song.app.song.music.tube.referrer;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import music.mp3.song.app.song.music.tube.BuildConfig;
import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.bean.BaseBean;
import music.mp3.song.app.song.music.tube.firebase.Referrer;


public abstract class BaseReferrer extends BaseBean implements Parcelable {

    public String title;
    public String subtitle;
    public String icon; // icon url
    public String poster; // large image url
    public String pkg;
    public String referrer; // referrer
    public String action; // button text
    public String country; //
    public String webappurl; //
    public String logourl; //

    public boolean isInvalid() {
        try {
            if (BuildConfig.DEBUG) {
                return false;
            }
            if (TextUtils.isEmpty(pkg) && TextUtils.isEmpty(webappurl)) {
                return true;
            }
            String phoneCountry = Referrer.getPhoneCountry(MusicApp.getInstance());
            if (TextUtils.isEmpty(phoneCountry)) {
                return true;
            }
            if (!TextUtils.isEmpty(country)) {
                if (!country.toLowerCase().contains(phoneCountry.toLowerCase())) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public String getPkg(boolean refer) {
        if (referrer == null) {
            referrer = "";
        } else {
            referrer = referrer.trim();
            if (!referrer.isEmpty() && !referrer.startsWith("&")) {
                referrer = "&" + referrer;
            }
        }
        return refer ? pkg + referrer : pkg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.subtitle);
        dest.writeString(this.icon);
        dest.writeString(this.poster);
        dest.writeString(this.pkg);
        dest.writeString(this.referrer);
        dest.writeString(this.action);
        dest.writeString(this.country);
        dest.writeString(this.webappurl);
        dest.writeString(logourl);
    }

    public BaseReferrer() {
    }

    protected BaseReferrer(Parcel in) {
        this.title = in.readString();
        this.subtitle = in.readString();
        this.icon = in.readString();
        this.poster = in.readString();
        this.pkg = in.readString();
        this.referrer = in.readString();
        this.action = in.readString();
        this.country = in.readString();
        this.webappurl = in.readString();
        this.logourl = in.readString();
    }

}
