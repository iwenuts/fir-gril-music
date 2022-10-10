package music.mp3.song.app.song.music.tube.network.yt;

import android.content.Context;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;

/**
 * 1.导入aar
 * 2.applicaion调用init
 * 3.使用的地方调用search和getAudios接口
 */
public class YTManager {
    private static YTManager instance = null;

    private YTManager() {

    }

    public static YTManager getInstance() {
        if (null == instance) {
            synchronized (YTManager.class) {
                if (null == instance) {
                    instance = new YTManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        NewPipe.init(DownloaderImpl.init(null),
                Constants.getPreferredLocalization(context),
                Constants.getPreferredContentCountry(context));
    }

    public void search(String query, YTSearch.onSearchListener listener) {
        new YTSearch().search(ServiceList.YouTube.getServiceId(), query, listener);
    }

    public void getAudios(String url, YTGetAudios.onAudiosListener listener) {
        new YTGetAudios().getAudios(ServiceList.YouTube.getServiceId(), url, listener);
    }

    public void searchSound(String query, YTSearch.onSearchListener listener) {
        new YTSearch().search(ServiceList.SoundCloud.getServiceId(), query, listener);
    }

    public void getAudiosSound(String url, YTGetAudios.onAudiosListener listener) {
        new YTGetAudios().getAudios(ServiceList.SoundCloud.getServiceId(), url, listener);
    }
}
