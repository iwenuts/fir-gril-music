package music.mp3.song.app.song.music.tube.ui;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.ztools.ShareUtils;


public class UpdateDialog {
    public void showDialog(final Activity activity, final String str, boolean force,String uInfo) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        ((Button) dialog.findViewById(R.id.btn_dialog)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!TextUtils.isEmpty(MusicApp.config.webappurl)) {
                    ShareUtils.openBrowser(activity, MusicApp.config.webappurl);
                } else {
                    ShareUtils.gotoGoogePlayStore(activity, !TextUtils.isEmpty(str) ? str : MusicApp.sContext.getPackageName());
                }
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.btnCancle);
        if (force) {
            cancel.setVisibility(View.GONE);
        } else {
            cancel.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

        TextView textDialog = dialog.findViewById(R.id.text_dialog);
        textDialog.setText(uInfo);
        dialog.show();
    }
}
