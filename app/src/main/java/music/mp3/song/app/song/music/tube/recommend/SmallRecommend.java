package music.mp3.song.app.song.music.tube.recommend;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import music.mp3.song.app.song.music.tube.R;
import com.yinglan.shadowimageview.ShadowImageView;

public class SmallRecommend extends FrameLayout {
    private ShadowImageView iconSiv;
    private ImageView closeIv;
    private Activity activity;
    private View view;
    private RecommendBean bean;

    public SmallRecommend(Context context) {
        super(context);
    }

    public SmallRecommend(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SmallRecommend(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* Access modifiers changed, original: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.iconSiv = (ShadowImageView) findViewById(R.id.recommend_icon);
        this.closeIv = (ImageView) findViewById(R.id.close_icon);
        this.view = findViewById(R.id.recommend_view);
    }

    /* Access modifiers changed, original: protected */
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.view.clearAnimation();
        this.activity = null;
    }

    public static SmallRecommend newInstance(Activity activity) {
        SmallRecommend smallRecommend = (SmallRecommend) LayoutInflater.from(activity).inflate(R.layout.small_recommend_layout, null);
        smallRecommend.activity = activity;
        return smallRecommend;
    }

    public void removeView() {
        try {
            if (!(this.activity == null || getParent() == null)) {
                ((ViewGroup) this.activity.findViewById(android.R.id.content)).removeView(this);
            }
            setVisibility(GONE);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void addView() {
        try {
            if (this.activity != null) {
                ((ViewGroup) this.activity.findViewById(android.R.id.content)).addView(this);
            }
            if (RecommendManager.getInstance().getRecomEventListener() != null) {
                RecommendManager.getInstance().getRecomEventListener().showed(1, this.bean);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public SmallRecommend build(final RecommendBean recommendBean) {
        this.bean = recommendBean;
//        this.iconSiv.setImageResource(recommendBean.getImgId());
        if (!TextUtils.isEmpty(recommendBean.getImgUrl())) {
            Glide.with(this)
                    .load(recommendBean.getImgUrl())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            iconSiv.setImageDrawable(resource);
                        }
                    });
        }
        this.iconSiv.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                RecommendManager.getInstance().getProvider().remove(recommendBean);
                RecommendUtils.gotoGP(recommendBean.getPackageIdWithRecom());
                SmallRecommend.this.removeView();
                if (RecommendManager.getInstance().getRecomEventListener() != null) {
                    RecommendManager.getInstance().getRecomEventListener().clicked(1, recommendBean);
                }
            }
        });
        this.closeIv.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SmallRecommend.this.removeView();
                RecommendUtils.setCount(recommendBean.getPackageId(), RecommendUtils.getMaxShowCount());
                if (RecommendManager.getInstance().getRecomEventListener() != null) {
                    RecommendManager.getInstance().getRecomEventListener().closed(1, recommendBean);
                }
            }
        });
        this.view.startAnimation(RecommendUtils.rotateIcon(7));
        return this;
    }
}
