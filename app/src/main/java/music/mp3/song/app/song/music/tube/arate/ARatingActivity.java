package music.mp3.song.app.song.music.tube.arate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;
import music.mp3.song.app.song.music.tube.ztools.ShareUtils;
import music.mp3.song.app.song.music.tube.ztools.ToastUtils;


public class ARatingActivity extends AppCompatActivity {
    private boolean mFinishWithAnimation = true;
    private ImageView[] startIVS = new ImageView[5];
    private BStarPointContainer[] starFrameLayouts = new BStarPointContainer[5];
    private AnimatorSet animatorSet;
    private String title;
    private String description;
    private static RatingClickListener sRatingClickListener;
    private int currentIndex = 0;

    /**
     * 启动
     *
     * @param context
     */
    public static void launch(Context context, RatingClickListener listener) {
        try {
            if (!willShow(context)) {
                return;
            }

            Intent intent = new Intent(context, ARatingActivity.class);
            if (context instanceof Activity) {
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            setRatingClickListener(listener);
        } catch (Throwable var2) {
            var2.printStackTrace();
        }
    }

    public static void launch(Context context, String title, String description, RatingClickListener listener) {
        try {
            if (!willShow(context)) {
                return;
            }
            Intent intent = new Intent(context, ARatingActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            if (context instanceof Activity) {
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            setRatingClickListener(listener);


        } catch (Throwable var4) {
            var4.printStackTrace();
        }
    }

    /**
     * 设置监听
     *
     * @param ratingClickListener
     */
    public static void setRatingClickListener(RatingClickListener ratingClickListener) {
        sRatingClickListener = ratingClickListener;
    }

    /**
     * 是否可以显示评分对话框---todo 逻辑
     *
     * @param context
     * @return
     */
    private static boolean willShow(Context context) {
        return true;
    }


    public ARatingActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rating);
        initView();
        FlurryEventReport.rateShow();
    }

    private void initView() {
        if (this.getIntent() != null) {
            this.title = this.getIntent().getStringExtra("title");
            this.description = this.getIntent().getStringExtra("description");
        }

        TextView titleTV = (TextView) this.findViewById(R.id.title_tv);
        if (TextUtils.isEmpty(this.title)) {
            titleTV.setText(String.format(this.getString(R.string.rating_title), new Object[]{this.getString(R.string.app_name)}));
        } else {
            titleTV.setText(this.title);
        }

        TextView descriptionTV = (TextView) this.findViewById(R.id.description_tv);
        if (!TextUtils.isEmpty(this.description)) {
            descriptionTV.setText(this.description);
        }

        final ImageView starIV1 = (ImageView) this.findViewById(R.id.star_iv1);
        BStarPointContainer starFrameLayout1 = (BStarPointContainer) this.findViewById(R.id.star1);
        this.startIVS[0] = starIV1;
        this.starFrameLayouts[0] = starFrameLayout1;
        ImageView starIV2 = (ImageView) this.findViewById(R.id.star_iv2);
        BStarPointContainer starFrameLayout2 = (BStarPointContainer) this.findViewById(R.id.star2);
        this.startIVS[1] = starIV2;
        this.starFrameLayouts[1] = starFrameLayout2;
        ImageView starIV3 = (ImageView) this.findViewById(R.id.star_iv3);
        BStarPointContainer starFrameLayout3 = (BStarPointContainer) this.findViewById(R.id.star3);
        this.startIVS[2] = starIV3;
        this.starFrameLayouts[2] = starFrameLayout3;
        ImageView starIV4 = (ImageView) this.findViewById(R.id.star_iv4);
        BStarPointContainer starFrameLayout4 = (BStarPointContainer) this.findViewById(R.id.star4);
        this.startIVS[3] = starIV4;
        this.starFrameLayouts[3] = starFrameLayout4;
        final ImageView starIV5 = (ImageView) this.findViewById(R.id.star_iv5);
        BStarPointContainer starFrameLayout5 = (BStarPointContainer) this.findViewById(R.id.star5);
        this.startIVS[4] = starIV5;
        this.starFrameLayouts[4] = starFrameLayout5;
        final ImageView handleIV = (ImageView) this.findViewById(R.id.handle_icon_iv);
        starIV1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT > 16) {
                    starIV1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    starIV1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                starIV1.postDelayed(new Runnable() {
                    public void run() {
                        ARatingActivity.this.handleMoveAnimation(handleIV, starIV5, new Runnable() {
                            public void run() {
                                startAnimationSpread();
                            }
                        });
                    }
                }, 600L);
            }
        });
    }

    private void handleMoveAnimation(ImageView handleIV, View targetView, final Runnable endRunnable) {
        int[] location = new int[2];
        targetView.getLocationInWindow(location);
        ObjectAnimator XAnimator = ObjectAnimator.ofFloat(handleIV, "x", new float[]{handleIV.getX(), (float) location[0]});
        XAnimator.setDuration(200L);
        ObjectAnimator YAnimator = ObjectAnimator.ofFloat(handleIV, "y", new float[]{handleIV.getY(), (float) location[1]});
        YAnimator.setDuration(200L);
        ObjectAnimator aplhaAnimator = ObjectAnimator.ofFloat(handleIV, "alpha", new float[]{1.0F, 0.0F});
        aplhaAnimator.setDuration(200L);
        aplhaAnimator.setStartDelay(200L);
        this.animatorSet = new AnimatorSet();
        this.animatorSet.playTogether(new Animator[]{XAnimator, YAnimator, aplhaAnimator});
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (endRunnable != null) {
                    endRunnable.run();
                }

            }
        });
        this.animatorSet.start();
    }

    private void startAnimationSpread() {
        this.starFrameLayouts[this.currentIndex].startPointSpread(this.startIVS[this.currentIndex], new Runnable() {
            public void run() {
                ARatingActivity.this.currentIndex++;
                if (ARatingActivity.this.currentIndex < 5) {
                    ARatingActivity.this.starFrameLayouts[ARatingActivity.this.currentIndex].startPointSpread(ARatingActivity.this.startIVS[ARatingActivity.this.currentIndex], this);
                } else {
                    ARatingActivity.this.currentIndex = 0;
                }

            }
        });
    }

    private void stopAnimationSpread() {
        BStarPointContainer[] var1 = this.starFrameLayouts;
        int var2 = var1.length;

        int var3;
        for (var3 = 0; var3 < var2; ++var3) {
            BStarPointContainer starPointContainer = var1[var3];
            starPointContainer.stopPointSpread();
        }

        ImageView[] var5 = this.startIVS;
        var2 = var5.length;

        for (var3 = 0; var3 < var2; ++var3) {
            ImageView starImg = var5[var3];
            starImg.setImageResource(R.drawable.bl_ic_rating_star);
        }
    }

    public void onRatingButton1to4Clicked(View v) {
        if (sRatingClickListener != null) {
            sRatingClickListener.onClick1To4Start();
        }
        ToastUtils.showShortToast("Thanks for your feedback!");
        this.mFinishWithAnimation = false;
        this.finish();
    }

    public void onCancelClicked(View v) {
        if (sRatingClickListener != null) {
            sRatingClickListener.onClickReject();
        }
        this.mFinishWithAnimation = false;
        this.finish();
    }

    public void onRatingButtonClicked(View v) {
        if (sRatingClickListener != null) {
            sRatingClickListener.onClickFiveStart();
        }
        this.mFinishWithAnimation = false;
        this.finish();
        ShareUtils.gotoGoogePlayStore(this, getPackageName());
    }

    public void finish() {
        super.finish();
        if (this.mFinishWithAnimation) {
            this.overridePendingTransition(R.anim.anim_slide_in_from_bottom, R.anim.slide_out_to_bottom_anim);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
//            this.finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void onDismissButtonClicked(View v) {
        this.finish();
    }

    protected void onDestroy() {
        this.stopAnimationSpread();
        if (this.animatorSet != null && this.animatorSet.isRunning()) {
            this.animatorSet.cancel();
        }
        super.onDestroy();
        sRatingClickListener = null;
    }

    public interface RatingClickListener {
        void onClickFiveStart();

        void onClick1To4Start();

        void onClickReject();
    }
}
