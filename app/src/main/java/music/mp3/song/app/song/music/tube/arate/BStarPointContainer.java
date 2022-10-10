package music.mp3.song.app.song.music.tube.arate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import music.mp3.song.app.song.music.tube.R;


public class BStarPointContainer extends FrameLayout {
    private Paint pointPaint;
    private int width;
    private int height;
    private int length;
    private int pointRadial = 0;
    private int point1Radian = 25;
    private int point2Radian = 90;
    private int point3Radian = 155;
    private int point4Radian = 230;
    private int point5Radian = 310;
    private float angle1;
    private float angle2;
    private float angle3;
    private float angle4;
    private float angle5;
    private boolean isDrawPoint = false;
    private AnimatorSet animatorSet;
    private int pointAlpha = 0;
    private long pointDuration = 150L;
    private long starDuration = 100L;

    public BStarPointContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public BStarPointContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    private void init() {
        this.setWillNotDraw(false);
        this.pointPaint = new Paint();
        this.pointPaint.setAntiAlias(true);
        this.pointPaint.setStrokeWidth((float)dp2px(this.getContext(), 5.0F));
        this.pointPaint.setStrokeCap(Paint.Cap.ROUND);
        this.pointPaint.setColor(Color.parseColor("#FF6600"));
        this.pointPaint.setAlpha(this.pointAlpha);
        this.angle1 = (float)((double)this.point1Radian * 3.141592653589793D / 180.0D);
        this.angle2 = (float)((double)this.point2Radian * 3.141592653589793D / 180.0D);
        this.angle3 = (float)((double)this.point3Radian * 3.141592653589793D / 180.0D);
        this.angle4 = (float)((double)this.point4Radian * 3.141592653589793D / 180.0D);
        this.angle5 = (float)((double)this.point5Radian * 3.141592653589793D / 180.0D);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        this.length = Math.max(this.width, this.height);
        Log.v("xx", " onSizeChanged width " + this.width + " height " + this.height);
    }

    public void startPointSpread(final ImageView starView, final Runnable finishRunnable) {
        new ObjectAnimator();
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(starView, "alpha", new float[]{0.0F, 1.0F});
        alphaAnimator.setDuration(this.starDuration);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(starView, "scaleX", new float[]{1.3F, 1.0F});
        scaleXAnimator.setDuration(this.starDuration);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(starView, "scaleY", new float[]{1.3F, 1.0F});
        scaleYAnimator.setDuration(this.starDuration);
        scaleYAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                starView.setImageDrawable(BStarPointContainer.this.getResources().getDrawable(R.drawable.bl_ic_rating_star));
            }
        });
        new ValueAnimator();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(new int[]{12, 17});
        valueAnimator.setDuration(this.pointDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = ((Integer)animation.getAnimatedValue()).intValue();
                BStarPointContainer.this.pointRadial = dp2px(BStarPointContainer.this.getContext(), (float)value);
                BStarPointContainer.this.invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                BStarPointContainer.this.isDrawPoint = true;
            }
        });
        valueAnimator.setStartDelay(this.starDuration);
        new ValueAnimator();
        ValueAnimator valueAnimatorAlphaIn = ValueAnimator.ofInt(new int[]{0, 255});
        valueAnimatorAlphaIn.setDuration(this.pointDuration);
        valueAnimatorAlphaIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = ((Integer)animation.getAnimatedValue()).intValue();
                BStarPointContainer.this.pointAlpha = value;
                BStarPointContainer.this.invalidate();
            }
        });
        valueAnimatorAlphaIn.setStartDelay(this.starDuration);
        new ValueAnimator();
        ValueAnimator valueAnimatorAlphaOut = ValueAnimator.ofInt(new int[]{255, 0});
        valueAnimatorAlphaOut.setDuration(50L);
        valueAnimatorAlphaOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = ((Integer)animation.getAnimatedValue()).intValue();
                BStarPointContainer.this.pointAlpha = value;
                BStarPointContainer.this.invalidate();
            }
        });
        valueAnimatorAlphaOut.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                BStarPointContainer.this.isDrawPoint = false;
                if(finishRunnable != null) {
                    finishRunnable.run();
                }

            }
        });
        valueAnimatorAlphaOut.setStartDelay(this.pointDuration + this.starDuration);
        this.animatorSet = new AnimatorSet();
        this.animatorSet.playTogether(new Animator[]{scaleXAnimator, scaleYAnimator, valueAnimator, valueAnimatorAlphaIn, valueAnimatorAlphaOut, alphaAnimator});
        this.animatorSet.start();
    }

    public void stopPointSpread() {
        if(this.animatorSet != null && this.animatorSet.isRunning()) {
            this.animatorSet.cancel();
            this.animatorSet = null;
        }

        this.isDrawPoint = false;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0);
        if(this.isDrawPoint) {
            canvas.save();
            this.pointPaint.setAlpha(this.pointAlpha);
            canvas.translate((float)(this.length / 2), (float)(this.length / 2));
            int x1 = (int)(Math.cos((double)this.angle1) * (double)((float)this.pointRadial * 1.0F));
            int y1 = (int)(Math.sin((double)this.angle1) * (double)((float)this.pointRadial * 1.0F));
            canvas.drawPoint((float)x1, (float)y1, this.pointPaint);
            int x2 = (int)(Math.cos((double)this.angle2) * (double)((float)this.pointRadial * 1.0F));
            int y2 = (int)(Math.sin((double)this.angle2) * (double)((float)this.pointRadial * 1.0F));
            canvas.drawPoint((float)x2, (float)y2, this.pointPaint);
            int x3 = (int)(Math.cos((double)this.angle3) * (double)((float)this.pointRadial * 1.0F));
            int y3 = (int)(Math.sin((double)this.angle3) * (double)((float)this.pointRadial * 1.0F));
            canvas.drawPoint((float)x3, (float)y3, this.pointPaint);
            int x4 = (int)(Math.cos((double)this.angle4) * (double)((float)this.pointRadial * 1.0F));
            int y4 = (int)(Math.sin((double)this.angle4) * (double)((float)this.pointRadial * 1.0F));
            canvas.drawPoint((float)x4, (float)y4, this.pointPaint);
            int x5 = (int)(Math.cos((double)this.angle5) * (double)((float)this.pointRadial * 1.0F));
            int y5 = (int)(Math.sin((double)this.angle5) * (double)((float)this.pointRadial * 1.0F));
            canvas.drawPoint((float)x5, (float)y5, this.pointPaint);
            canvas.restore();
        }

    }

    public static int dp2px(Context context, float dp) {
        return (int) (0.5F + context.getResources().getDisplayMetrics().density * dp);
    }
}
