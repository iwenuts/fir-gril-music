package music.mp3.song.app.song.music.tube.recommend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.ztools.ShareUtils;


public class RecommendDialogActivity extends Activity {
    private RecommendBean recommendBean;
    private OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View view) {
            RecommendManager.getInstance().getProvider().remove(RecommendDialogActivity.this.recommendBean);
            String weburl = recommendBean.getWebappurl();
            if (!TextUtils.isEmpty(weburl)) {
                ShareUtils.openBrowser(RecommendDialogActivity.this, MusicApp.config.webappurl);
            } else {
                RecommendUtils.gotoGP(RecommendDialogActivity.this.recommendBean.getPackageIdWithRecom());
            }
            RecommendDialogActivity.this.finish();
            if (RecommendManager.getInstance().getRecomEventListener() != null) {
                RecommendManager.getInstance().getRecomEventListener().clicked(2, RecommendDialogActivity.this.recommendBean);
            }
        }
    };

    public static void show(Context context, RecommendBean recommendBean) {
        try {
            Intent intent = new Intent(context, RecommendDialogActivity.class);
            intent.putExtra("recommendBean", recommendBean);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.recommendBean != null) {
            bundle.putParcelable("recommendBean", this.recommendBean);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (RecommendManager.getInstance().getRecomEventListener() != null) {
            RecommendManager.getInstance().getRecomEventListener().closed(2, this.recommendBean);
        }
    }

    public static void setStyle(Activity activity) {
        if (VERSION.SDK_INT >= 21) {
            activity.getWindow().getDecorView().setSystemUiVisibility(1280);
            activity.getWindow().setStatusBarColor(0);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            setStyle((Activity) this);
            setContentView(R.layout.recommend_dialog_activity);
            if (bundle == null) {
                this.recommendBean = (RecommendBean) getIntent().getParcelableExtra("recommendBean");
            } else {
                this.recommendBean = (RecommendBean) bundle.getParcelable("recommendBean");
            }
            findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    RecommendUtils.setCount(RecommendDialogActivity.this.recommendBean.getPackageId(), RecommendUtils.getMaxShowCount());
                    RecommendDialogActivity.this.finish();
                    if (RecommendManager.getInstance().getRecomEventListener() != null) {
                        RecommendManager.getInstance().getRecomEventListener().closed(2, RecommendDialogActivity.this.recommendBean);
                    }
                }
            });
            ImageView imageView = (ImageView) findViewById(R.id.recom_app_icon);
//            imageView.setImageResource(this.recommendBean.getImgId());
            if (!TextUtils.isEmpty(this.recommendBean.getImgUrl())) {
                Glide.with(this)
                        .load(this.recommendBean.getImgUrl())
                        .into(imageView);
            }
            imageView.setOnClickListener(this.onClickListener);
            ((TextView) findViewById(R.id.iv_app_name)).setText(this.recommendBean.getTitle());
            ((StarLevelLayoutView) findViewById(R.id.recom_app_star)).setRating(5);
            ((TextView) findViewById(R.id.recom_app_desc)).setText(this.recommendBean.getDesc());
            findViewById(R.id.recom_cta_tv).setOnClickListener(this.onClickListener);
            if (RecommendManager.getInstance().getRecomEventListener() != null) {
                RecommendManager.getInstance().getRecomEventListener().showed(2, this.recommendBean);
            }
        } catch (Throwable th) {
            th.printStackTrace();
            finish();
        }
    }
}
