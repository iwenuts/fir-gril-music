package music.mp3.song.app.song.music.tube.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import music.mp3.song.app.song.music.tube.admax.MaxSearchNative;

public
class NativeTemplatesFrameLayout extends FrameLayout {
    public NativeTemplatesFrameLayout(@NonNull Context context) {
        super(context);
    }

    public NativeTemplatesFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NativeTemplatesFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void loadAd() {
        new MaxSearchNative(getContext()).createNativeAd(this);
    }


}
