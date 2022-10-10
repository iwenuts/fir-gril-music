package music.mp3.song.app.song.music.tube.admax;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import music.mp3.song.app.song.music.tube.R;


/**
 * Created by LiJiaZhi on 16/12/31.
 * 插屏 ad loading对话框
 */

public class AdLoadingDialog extends Dialog {

    public AdLoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public AdLoadingDialog(Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ad_loading);
    }
}
