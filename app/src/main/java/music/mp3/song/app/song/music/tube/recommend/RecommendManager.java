package music.mp3.song.app.song.music.tube.recommend;

import android.app.Activity;
import android.content.Context;

/* compiled from: RecommendManager */
public class RecommendManager {
    public static Context context;
    private static volatile RecommendManager recommendManager;
    private RecommendEventListener RecommendEventListener;
    private String packageIds = "";
    private static String recomSource = "";
    private DataProvider popupDataProvider = null;
    private DataProvider mainDataProvider = null;

    private RecommendManager() {
    }

    public static void init(Context context, String recomSource) {
        RecommendManager.context = context;
        RecommendManager.recomSource = recomSource;
        RecommendUtils.init(context);
    }

    public static String getRecomSource() {
        return recomSource;
    }

    public void setProvider(String packageIds) {
        if (packageIds == null) {
            packageIds = "";
        }
        if (packageIds.equals(this.packageIds)) {
            return;
        }
        this.packageIds = packageIds;
        popupDataProvider = new DataProvider(packageIds);
        mainDataProvider = new DataProvider(packageIds);
    }

    /* Access modifiers changed, original: protected */
    DataProvider getProvider() {
        return this.popupDataProvider;
    }

    RecommendEventListener getRecomEventListener() {
        return this.RecommendEventListener;
    }

    public static RecommendManager getInstance() {
        if (recommendManager == null) {
            synchronized (RecommendManager.class) {
                if (recommendManager == null) {
                    recommendManager = new RecommendManager();
                }
            }
        }
        return recommendManager;
    }

    public RecommendBean getMainRecommend() {
        return this.mainDataProvider != null ? this.mainDataProvider.getBean() : null;
    }

    public boolean showSmallRecommend(Activity activity) {
        if (activity == null || popupDataProvider == null) {
            return false;
        }
        try {
            if (activity.isFinishing()) {
                return false;
            }
            RecommendBean recommendBean = this.popupDataProvider.getBean();
            if (!canShow(recommendBean)) {
                return false;
            }
            SmallRecommend.newInstance(activity).build(recommendBean).addView();
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    public boolean showRecommend(Context context) {
        if (context == null || popupDataProvider == null) {
            return false;
        }
        RecommendBean bean = this.popupDataProvider.getBean();
        if (!canShow(bean)) {
            return false;
        }
        RecommendDialogActivity.show(context, bean);
        return true;
    }

    private boolean canShow(RecommendBean recommendBean) {
        if (recommendBean == null) {
            return false;
        }
        int showCount = RecommendUtils.getCount(recommendBean.getPackageId());
        if (showCount >= RecommendUtils.getMaxShowCount() || !RecommendUtils.getCanClick(recommendBean.getPackageId())) {
            this.popupDataProvider.remove(recommendBean);
            return false;
        }
        int i = showCount + 1;
        if (i == RecommendUtils.getMaxShowCount()) {
            this.popupDataProvider.remove(recommendBean);
        }
        RecommendUtils.setCount(recommendBean.getPackageId(), i);
        return true;
    }
}
