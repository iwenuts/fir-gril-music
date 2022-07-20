package song.downloader.music.tube.arate;

import android.content.Context;

import song.downloader.music.tube.firebase.FlurryEventReport;
import song.downloader.music.tube.ztools.vPrefsUtils;


public class RateManager {
    private static volatile RateManager instance;
    private static final int totalCount = 2;

    public static RateManager getInstance() {
        if (instance == null) {
            synchronized (RateManager.class) {
                if (instance == null) {
                    instance = new RateManager();
                }
            }
        }
        return instance;
    }

    private RateManager() {

    }

    public void tryRate(Context context) {
        if (!isShowRate()) {
            return;
        }
        incRateCount(1);
        ARatingActivity.launch(context, new ARatingActivity.RatingClickListener() {
            @Override
            public void onClickFiveStart() {
                incRateCount(totalCount);
                FlurryEventReport.rateClick("5");
            }

            @Override
            public void onClick1To4Start() {
                FlurryEventReport.rateClick("1to4");
            }

            @Override
            public void onClickReject() {
                FlurryEventReport.rateClick("cancel");
            }
        });
    }

    public void tryRateFinish(Context context) {
        if (!isShowRate()) {
            return;
        }
        incRateCount(1);
        ARatingActivity.launch(context, "Music Downloader", "For you to download Music, Please rate us Five Stars", new ARatingActivity.RatingClickListener() {
            @Override
            public void onClickFiveStart() {
                incRateCount(totalCount);
            }

            @Override
            public void onClick1To4Start() {
            }

            @Override
            public void onClickReject() {
            }
        });
    }

    public boolean isShowRate() {
        RateBean bean = vPrefsUtils.getRateBean();
        if (null == bean) {
            return true;
        }

        if (bean.nextTime < totalCount) {
            return true;
        } else {
            return false;
        }
    }

    private void incRateCount(int inc) {
        RateBean bean = vPrefsUtils.getRateBean();
        if (null == bean) {
            bean = new RateBean();
            bean.nextTime = 0;
        }
        bean.nextTime += inc;
        vPrefsUtils.setRateBean(bean);
    }

}
