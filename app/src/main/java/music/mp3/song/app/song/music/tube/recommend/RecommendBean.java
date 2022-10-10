package music.mp3.song.app.song.music.tube.recommend;

import android.os.Parcel;
import android.os.Parcelable;

import music.mp3.song.app.song.music.tube.firebase.Referrer;

public class RecommendBean implements Parcelable {
    public static final Creator<RecommendBean> CREATOR = new Creator<RecommendBean>() {
        /* renamed from: DataProvider */
        public RecommendBean createFromParcel(Parcel parcel) {
            return new RecommendBean(parcel);
        }

        /* renamed from: DataProvider */
        public RecommendBean[] newArray(int i) {
            return new RecommendBean[i];
        }
    };
//    private int imgId;
    private String imgUrl;
    private String title;
    private String packageId;
    private String webappurl;
    private String desc;
    private int weight;
    private String superUser;
    private StringBuilder stringBuilder;

    public String getWebappurl() {
        return webappurl == null ? "" : webappurl;
    }

    public void setWebappurl(String webappurl) {
        this.webappurl = webappurl;
    }

    public int getWeight() {
        return this.weight;
    }

//    public int getImgId() {
//        return this.imgId;
//    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDesc() {
        return this.desc;
    }

    public boolean equals(Object obj) {
        return (obj instanceof RecommendBean) && ((RecommendBean) obj).getPackageId().equals(getPackageId());
    }

//    public RecommendBean(int i, String str, String str2, int i2, String... strArr) {
//        this.imgId = i;
//        this.title = str;
//        this.desc = str2;
//        this.packageId = strArr;
//        this.weight = i2;
//    }

    //promote: weight%%super%%packageId%%title%%desc%%imgUrl
    public RecommendBean(String packageStr) {
        String[] sections = packageStr.split("%%");
        try {
            this.weight = Integer.parseInt(sections[0]);
        } catch (Throwable e) {
            this.weight = 10;
        }
        this.superUser = sections[1];
        this.packageId = sections[2];
        this.title = sections[3];
        this.desc = sections.length >= 5 ? sections[4] : "";
        this.imgUrl = sections.length >= 6 ? sections[5] : "";
        this.webappurl = sections.length >= 7 ?sections[6] : "";
    }

    public String getPackageId() {
        return packageId;
    }

    public String getPackageIdWithRecom() {
//        return getPackageId() + "&referrer=utm_source%3DappRecommend%26utm_medium%3D" + Referrer.stringToMD5(getPackageId()) + "%26utm_campaign%3DmusicDev02";
        return "0".equals(superUser) ? getPackageId() : getPackageId() + "&referrer=utm_source%3DappRecommend%26utm_medium%3D" + Referrer.stringToMD5(getPackageId()) + "%26utm_campaign%3D" + RecommendManager.getRecomSource();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.imgUrl);
        parcel.writeString(this.title);
        parcel.writeString(this.desc);
        parcel.writeString(this.packageId);
        parcel.writeInt(this.weight);
        parcel.writeString(this.superUser);
        parcel.writeSerializable(this.stringBuilder);
        parcel.writeString(this.webappurl);
    }

    protected RecommendBean(Parcel parcel) {
        this.imgUrl = parcel.readString();
        this.title = parcel.readString();
        this.desc = parcel.readString();
        this.packageId = parcel.readString();
        this.weight = parcel.readInt();
        this.superUser = parcel.readString();
        this.stringBuilder = (StringBuilder) parcel.readSerializable();
        this.webappurl = parcel.readString();
    }
}
