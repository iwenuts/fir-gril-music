package music.mp3.song.app.song.music.tube.dialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.ButterKnife;
import music.mp3.song.app.song.music.tube.BuildConfig;
import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.base.BaseDialogFragment;
import music.mp3.song.app.song.music.tube.referrer.ReferrerStream;
import music.mp3.song.app.song.music.tube.referrer.SpecialReferrer;

public class QualityDialog extends BaseDialogFragment {
    TextView titleTv;
    TextView common2Tv;
    TextView common3Tv;
    String mTitle;
    private String adUrl = "";

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_quality;
    }

    @Override
    public void bindView(View v) {
        ButterKnife.bind(this, v);
        titleTv = v.findViewById(R.id.title_tv);
        common2Tv = v.findViewById(R.id.common2_tv);
        common3Tv = v.findViewById(R.id.common3_tv);
        if (!TextUtils.isEmpty(mTitle)) {
            titleTv.setText(mTitle);
        }

        v.findViewById(R.id.common1_ll).setOnClickListener(view -> {
            if (mOnSelectListner != null) {
                mOnSelectListner.mOnSelectListner(1);
            }
            dismiss();
        });
        v.findViewById(R.id.common2_ll).setOnClickListener(view -> {

            if (mOnSelectListner != null) {
                mOnSelectListner.mOnSelectListner(2);
            }
            dismiss();
        });
        v.findViewById(R.id.common3_ll).setOnClickListener(view -> {
            if (mOnSelectListner != null) {
                mOnSelectListner.mOnSelectListner(3);
            }
            dismiss();
        });
        View adLayout = v.findViewById(R.id.ad_layout);
        adLayout.setOnClickListener(v1 -> {
            if (mOnSelectListner != null) {
                mOnSelectListner.mOnSelectAdListener(adUrl);
            }
        });
        ImageView tubeIconImage = v.findViewById(R.id.icon_image);
        TextView adTitle = v.findViewById(R.id.ad_title);
        TextView adSubmitTitle = v.findViewById(R.id.ad_submit_title);

        ImageView adImage = v.findViewById(R.id.ad_image);

        View adItemLayout = v.findViewById(R.id.ad_item_layout);

        ReferrerStream referrerStream = MusicApp.config.referrer;
        if (BuildConfig.DEBUG) {
            referrerStream = new ReferrerStream();
            referrerStream.player_tube = new SpecialReferrer();
//            referrerStream.player_tube.title = "title1";
//            referrerStream.player_tube.subtitle = "subtitle";
            referrerStream.player_tube.icon = "https://tse4-mm.cn.bing.net/th/id/OIP-C.KDaNo3vPbY2LAJzEa2zhlAHaCU?pid=ImgDet&rs=1";
            MusicApp.config.referrer = referrerStream;
        }

        if (referrerStream != null && referrerStream.player_tube != null) {
            SpecialReferrer playerTubeReferrer = referrerStream.player_tube;
            adLayout.setVisibility(View.VISIBLE);
            String title = playerTubeReferrer.title;
            String subtitle = playerTubeReferrer.subtitle;
            String icon = playerTubeReferrer.icon;
            if (!TextUtils.isEmpty(title)) {
                adItemLayout.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(icon).into(tubeIconImage);
                adTitle.setText(title);
                adSubmitTitle.setText(subtitle);
            } else {
                adImage.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(icon).into(adImage);
            }
            if (TextUtils.isEmpty(playerTubeReferrer.webappurl)) {
                adUrl = playerTubeReferrer.getPkg(true);
            } else {
                adUrl = playerTubeReferrer.webappurl;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private OnSelectListner mOnSelectListner;

    public void setOnSelectListner(OnSelectListner OnSelectListner) {
        mOnSelectListner = OnSelectListner;
    }

    public void setTitle(String title) {
        mTitle = title;
    }


    public interface OnSelectListner {
        void mOnSelectListner(int index);

        void mOnSelectAdListener(String adurl);
    }


}
