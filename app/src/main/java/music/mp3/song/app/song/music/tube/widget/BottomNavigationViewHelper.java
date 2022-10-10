package music.mp3.song.app.song.music.tube.widget;

import android.annotation.SuppressLint;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// 利用反射，改变 item 中 mShiftingMode 的值
public class BottomNavigationViewHelper {

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView navigationView) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        menuView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        menuView.buildMenuView();
    }
}