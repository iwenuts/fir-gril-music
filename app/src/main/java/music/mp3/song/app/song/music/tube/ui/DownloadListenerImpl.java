package music.mp3.song.app.song.music.tube.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.bean.ZlDownloadEvent;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.admax.MaxDownloadInterstitial;
import music.mp3.song.app.song.music.tube.ztools.FileUtil;
import music.mp3.song.app.song.music.tube.ztools.ToastUtils;
import music.mp3.song.app.song.music.tube.ztools.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.text.NumberFormat;

public class DownloadListenerImpl implements MyDownloadManager.DownloadListener {

    private MaterialDialog mDownloadDialog;
    private Music mCurrentDownloadBean;

    private TextView mProgressLabel;
    private TextView mProgressMinMax;


    public DownloadListenerImpl() {
        EventBus.getDefault().register(this);
    }

    private Resources getResources() {
        return MusicApp.getInstance().getResources();
    }

    @Override
    public void download(Music bean) {
        mCurrentDownloadBean = bean;
        Activity topAct = MusicApp.getInstance().getTopActivity();
        if (topAct == null || topAct.isFinishing()) {
            return;
        }
        try {
            new MaterialDialog.Builder(topAct)
                    .widgetColor(getResources().getColor(R.color.colorPrimary))
                    .titleColor(getResources().getColor(R.color.black))
                    .title(String.format(getResources().getString(R.string.download_dialog_title),
                            bean.title))
                    .content(R.string.download_dialog_content)
                    .contentGravity(GravityEnum.CENTER)
                    .negativeText("Cancel")
                    .negativeColor(Color.GRAY)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.cancel();
                        }
                    })
                    .progress(false, 100, true)
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            MyDownloadManager.getInstance().removeTask(bean);

                            ZlDownloadEvent event = new ZlDownloadEvent();
                            event.status = Music.DL_CANCEL;
                            event.progress = 0;
                            event.bean = bean;
                            EventBus.getDefault().postSticky(event);
                        }
                    })
                    .cancelable(false)
                    .showListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            mDownloadDialog = (MaterialDialog) dialogInterface;
                            if (topAct != null) {
                                if (mDownloadDialog.getProgressBar() != null) {
                                    ViewGroup.LayoutParams layoutParams = mDownloadDialog.getProgressBar().getLayoutParams();
                                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                    layoutParams.height = (int) ViewUtil.convertDpToPixel(3, MusicApp.getInstance().getResources());
                                    mDownloadDialog.getProgressBar().setLayoutParams(layoutParams);
                                }

                                try {
                                    Drawable drawable = MusicApp.getInstance().getResources().getDrawable(R.drawable.f_down_progress);
                                    mDownloadDialog.getProgressBar().setProgressDrawable(drawable);

                                    Class<?> clazz = mDownloadDialog.getClass();
                                    Field fieldProgressLabel = clazz.getDeclaredField("progressLabel");
                                    fieldProgressLabel.setAccessible(true);
                                    mProgressLabel = (TextView) fieldProgressLabel.get(mDownloadDialog);
                                    mProgressLabel.setText("0KB");
                                    Field fieldProgressMinMax = clazz.getDeclaredField("progressMinMax");
                                    fieldProgressMinMax.setAccessible(true);
                                    mProgressMinMax = (TextView) fieldProgressMinMax.get(mDownloadDialog);
                                    mProgressMinMax.setText("0%");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }

                        }
                    }).show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#F7E000"));
    ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.parseColor("#2EC000"));

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ZlDownloadEvent event) {
        if (null != mDownloadDialog && mCurrentDownloadBean != null && event.bean != null
                && event.bean.downloadUrl.equals(mCurrentDownloadBean.downloadUrl)) {
            if (event.status == Music.DL_DONE) {
                ToastUtils.showShortToast("Download Success!");
                mDownloadDialog.dismiss();
                deal();
                mDownloadDialog = null;
            } else if (event.status == Music.DL_DOING) {
                int bytes = event.soBytes;
                int slowBytes = bytes / 3;
                int progress = event.progress;
                int slow = progress / 3;
                try {
                    float f = (float) progress / (float) mDownloadDialog.getMaxProgress();
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setMaximumFractionDigits(2);
                    String result = numberFormat.format(f * 100);
                    mProgressMinMax.setText(result + "%");

                    String lowText = FileUtil.getSizeDescription(slowBytes);
                    String heightText = FileUtil.getSizeDescription(slowBytes * 2);
                    String s = "+";
                    String content = lowText + s + heightText;
                    SpannableString spannableString = new SpannableString(content);
                    spannableString.setSpan(colorSpan1, 0, lowText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(colorSpan2, content.length() - (heightText.length() + s.length()), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                    mProgressLabel.setText(spannableString);
                    mDownloadDialog.getProgressBar().setProgress(slow);
                    mDownloadDialog.getProgressBar().setSecondaryProgress(progress);
                } catch (Exception e) {

                }

            } else if (event.status == Music.DL_ERROR) {
                ToastUtils.showShortToast("Download Error!");
                mDownloadDialog.dismiss();
                mDownloadDialog = null;
            }
        }
    }

    private void deal() {
        mDownloadDialog = null;
//        AdManager.getInstance().tryShowDownloadWithRate();
        MaxDownloadInterstitial.getInstance().show();
    }

}
