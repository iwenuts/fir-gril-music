package music.mp3.song.app.song.music.tube.network.yt;

import androidx.annotation.NonNull;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.search.SearchInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class YTSearch {
    private Disposable searchDisposable;
    private final CompositeDisposable searchdisposables = new CompositeDisposable();

    private onSearchListener mListener;
    public AtomicBoolean isLoading = new AtomicBoolean();

    public void search(int serviceId, String searchString, onSearchListener listener) {
        mListener = listener;
        List<String> contentFilter = new ArrayList<>(1);
        contentFilter.add("videos");
        if (searchdisposables != null) searchdisposables.clear();
        if (searchDisposable != null) searchDisposable.dispose();
        searchDisposable = ExtractorHelper.searchFor(serviceId,
                searchString,
                contentFilter,
                "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEvent((searchResult, throwable) -> isLoading.set(false))
                .subscribe(this::handleResult, this::onError);
    }


    private void handleResult(@NonNull SearchInfo result) {
        final List<Throwable> exceptions = result.getErrors();
        if (!exceptions.isEmpty() && !(exceptions.size() == 1 && exceptions.get(0) instanceof SearchExtractor.NothingFoundException)) {
            if (null != mListener) {
                mListener.onError();
            }
            return;
        }

        List<InfoItem> datas = result.getRelatedItems();
        if (null == datas || datas.size() == 0) {
            if (null != mListener) {
                mListener.onEmpty();
            }
            return;
        }
        if (null != mListener) {
            List<StreamInfoItem> res = new ArrayList<>();
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i) instanceof StreamInfoItem) {
                    res.add((StreamInfoItem) datas.get(i));
                }
            }
            mListener.onSuccess(res);
        }
    }

    private void onError(Throwable exception) {
        if (null != mListener) {
            mListener.onError();
        }
    }

    public interface onSearchListener {
        void onSuccess(List<StreamInfoItem> list);

        void onError();

        void onEmpty();
    }
}
