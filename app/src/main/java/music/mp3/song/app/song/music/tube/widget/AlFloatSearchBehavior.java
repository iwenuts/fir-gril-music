package music.mp3.song.app.song.music.tube.widget;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.arlib.floatingsearchview.FloatingSearchView;

/**
 * Created by liyanju on 2018/5/22.
 */

public class AlFloatSearchBehavior extends CoordinatorLayout.Behavior<FloatingSearchView> {

    public AlFloatSearchBehavior() {
        super();
    }

    public AlFloatSearchBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingSearchView child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            ViewCompat.setElevation(child, ViewCompat.getElevation(dependency));
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingSearchView child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            child.setTranslationY(dependency.getY());
            return true;
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }

}
