package music.mp3.song.app.song.music.tube.recommend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import music.mp3.song.app.song.music.tube.R;


public class StarLevelLayoutView extends LinearLayout {
    public StarLevelLayoutView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StarLevelLayoutView(Context context) {
        super(context);
    }

    @SuppressLint({"NewApi"})
    public StarLevelLayoutView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setRating(int i) {
        removeAllViews();
        if (i == 0) {
            i = 5;
        }
        for (int i2 = 0; i2 < 5; i2++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setPadding(RecommendUtils.dp2px(getContext(), 2.0f), 0, RecommendUtils.dp2px(getContext(), 2.0f), 0);
            LayoutParams layoutParams = new LayoutParams(RecommendUtils.dp2px(getContext(), 18.0f), RecommendUtils.dp2px(getContext(), 18.0f));
            if (i2 < i) {
                imageView.setImageResource(R.drawable.recom_star_sel);
            } else {
                imageView.setImageResource(R.drawable.zl_recom_star_nor);
            }
            addView(imageView, layoutParams);
        }
    }
}
