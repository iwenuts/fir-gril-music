package music.mp3.song.app.song.music.tube.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import music.mp3.song.app.song.music.tube.R;

/**
 * On 2019-03-25
 */
public class RoundRectLayout extends FrameLayout {

    public RoundRectLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public RoundRectLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundRectLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private float radius = 0;
    private Path mPath;
    private RectF rectF;
    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLeftRadius;
    private float bottomRightRadius;
    private Paint roundPaint;
    private Paint savePaint;

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundRectLayout);
        radius = a.getDimension(R.styleable.RoundRectLayout_android_radius, 0);
        a.recycle();
        setRadius(radius);
        mPath = new Path();
        rectF = new RectF();
        roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roundPaint.setColor(0);
        roundPaint.setAntiAlias(true);
        roundPaint.setStyle(Paint.Style.FILL);
        roundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        savePaint = new Paint();
        savePaint.setXfermode(null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setRadius(float radius) {
        this.radius = radius;
        topLeftRadius = radius;
        topRightRadius = radius;
        bottomLeftRadius = radius;
        bottomRightRadius = radius;
        postInvalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        rectF.setEmpty();
        rectF.set(0, 0, canvas.getWidth(), canvas.getHeight());
        int sc = canvas.saveLayer(rectF, savePaint, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);

        drawTopLeft(canvas);//用PorterDuffXfermode
        drawTopRight(canvas);//用PorterDuffXfermode
        drawBottomLeft(canvas);//用PorterDuffXfermode
        drawBottomRight(canvas);//用PorterDuffXfermode
        canvas.restoreToCount(sc);
    }

    private void drawTopLeft(Canvas canvas) {
        if (topLeftRadius > 0) {
            mPath.reset();
            rectF.setEmpty();
            mPath.moveTo(0, topLeftRadius);
            mPath.lineTo(0, 0);
            mPath.lineTo(topLeftRadius, 0);
            rectF.set(0, 0, topLeftRadius * 2, topLeftRadius * 2);
            mPath.arcTo(rectF, -90, -90);
            mPath.close();
            canvas.drawPath(mPath, roundPaint);
        }
    }

    private void drawTopRight(Canvas canvas) {
        if (topRightRadius > 0) {
            int width = getWidth();
            mPath.reset();
            rectF.setEmpty();
            mPath.moveTo(width - topRightRadius, 0);
            mPath.lineTo(width, 0);
            mPath.lineTo(width, topRightRadius);
            rectF.set(width - 2 * topRightRadius, 0,
                    width, topRightRadius * 2);
            mPath.arcTo(rectF, 0, -90);
            mPath.close();
            canvas.drawPath(mPath, roundPaint);
        }
    }

    private void drawBottomLeft(Canvas canvas) {
        if (bottomLeftRadius > 0) {
            int height = getHeight();
            mPath.reset();
            rectF.setEmpty();
            mPath.moveTo(0, height - bottomLeftRadius);
            mPath.lineTo(0, height);
            mPath.lineTo(bottomLeftRadius, height);
            rectF.set(0, height - 2 * bottomLeftRadius,
                    bottomLeftRadius * 2, height);
            mPath.arcTo(rectF, 90, 90);
            mPath.close();
            canvas.drawPath(mPath, roundPaint);
        }
    }

    private void drawBottomRight(Canvas canvas) {
        if (bottomRightRadius > 0) {
            int height = getHeight();
            int width = getWidth();
            mPath.reset();
            rectF.setEmpty();
            mPath.moveTo(width - bottomRightRadius, height);
            mPath.lineTo(width, height);
            mPath.lineTo(width, height - bottomRightRadius);
            rectF.set(width - 2 * bottomRightRadius,
                    height - 2 * bottomRightRadius, width, height);
            mPath.arcTo(rectF, 0, 90);
            mPath.close();
            canvas.drawPath(mPath, roundPaint);
        }
    }
}
