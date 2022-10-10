package music.mp3.song.app.song.music.tube.ztools;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.network.ApiConstants;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.admax.MaxRewardedAds;

public class AdRewardedDialog implements MaxRewardedAds.MaxRewardLoadedInterface {
    private static final long TIMER_DELAY = 1000 * 10;
    private boolean dialogAdError = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private MaterialDialog adDialog;
    private MaxRewardedAds mMaxRewardedAds;
    private OnRewardCallback adRewardCallback;
    private boolean mRewarded = false;
    private boolean mCanceled = false;


    public interface OnRewardCallback {
        void onReward();
        void onError();
    }

    public AdRewardedDialog(MaxRewardedAds maxRewardedAds, OnRewardCallback callback) {
        this.mMaxRewardedAds = maxRewardedAds;
        this.adRewardCallback = callback;
    }

    private void clearDialog() {
        try {
            if (adDialog != null) {
                if (adDialog.isShowing()) {
                    adDialog.dismiss();
                }
                adDialog = null;
            }
        } catch (Exception e) {

        }

        stopTimer();
        MaxRewardedAds.getInstance().setMaxRewardLoadedInterface(null);
        if (adRewardCallback != null) {
            if (mRewarded) {
                adRewardCallback.onReward();
            } else if (dialogAdError) {
                adRewardCallback.onError();
            } else if (mCanceled) {
            }
        }
        adRewardCallback = null;
    }

    //default for asking watch
    public void show(Activity context, Music bean) {
        if (adDialog != null) {
            return;
        }
        adDialog = new MaterialDialog.Builder(context)
                .title(bean.title)
                .iconRes(R.drawable.crown)
                .autoDismiss(false)
                .progress(true, 0)
                .positiveColorRes(R.color.purple)
                .positiveText("Watch Ad")
                .negativeText("Close")
                .negativeColor(Color.GRAY)
                .content("* Watch a Video and download High Quality Audios")
                .contentColorRes(R.color.purple)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        clearDialog();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mMaxRewardedAds.setMaxRewardLoadedInterface(AdRewardedDialog.this);
                        if (mMaxRewardedAds.isReady()) {
                            mMaxRewardedAds.show();
                        } else {
                            dialogAdLoading();
                            mMaxRewardedAds.loadAd();
                        }
                    }
                })
                .show();
        adDialog.getProgressBar().setVisibility(View.GONE);
    }

    private void dialogAdError() {
        stopTimer();
        dialogAdError = true;
        if (adDialog == null) {
            return;
        }
        //show error
        adDialog.getProgressBar().setVisibility(View.GONE);
        adDialog.setCancelable(false);
        adDialog.setContent("No ad available, skip ad and press ok to download..");
        adDialog.setActionButton(DialogAction.POSITIVE, null);
        adDialog.setActionButton(DialogAction.NEGATIVE, "OK");
//        adDialog.dismiss();
    }

    private void dialogAdReady() {
        stopTimer();
        if (adDialog == null) {
            return;
        }
        adDialog.getProgressBar().setVisibility(View.GONE);
//        adRewardedVideo.tryShow();
        mMaxRewardedAds.show();
    }

    private void dialogAdLoading() {
        adDialog.getProgressBar().setVisibility(View.VISIBLE);
        adDialog.setCancelable(false);
        adDialog.setContent("Loading.. Please wait");
        adDialog.setActionButton(DialogAction.POSITIVE, null);
        adDialog.setActionButton(DialogAction.NEGATIVE, null);
        startTimer();
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogAdError();
            }
        }, TIMER_DELAY);
    }

    private void stopTimer() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onAdLoaded() {
        if (!mRewarded) {
            dialogAdReady();
        } else {
            if (adDialog != null) {
                adDialog.dismiss();
            }
        }
    }

    @Override
    public void onReward() {
        mRewarded = true;

        ApiConstants.rewarded = true;
        ApiConstants.sDownloadQuota = MusicApp.config.rewardpop;

        //show ok
        if (adDialog != null) {
            adDialog.getProgressBar().setVisibility(View.GONE);
            adDialog.setCancelable(false);
            adDialog.setContent("Unlocked ! press ok to download..");
            adDialog.setActionButton(DialogAction.POSITIVE, null);
            adDialog.setActionButton(DialogAction.NEGATIVE, "OK");
        }
    }

    @Override
    public void onAdClosed() {
        mCanceled = !mRewarded;
        //show cancel
        if (mCanceled && adDialog != null) {
            adDialog.getProgressBar().setVisibility(View.GONE);
            adDialog.setCancelable(false);
            adDialog.setContent("Watch canceled, try again later !");
            adDialog.setActionButton(DialogAction.POSITIVE, null);
            adDialog.setActionButton(DialogAction.NEGATIVE, "OK");
        }
//        adDialog.dismiss();
    }

    @Override
    public void onError() {
        dialogAdError();
    }

}
