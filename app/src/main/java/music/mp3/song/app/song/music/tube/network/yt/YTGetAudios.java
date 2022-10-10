package music.mp3.song.app.song.music.tube.network.yt;

import androidx.annotation.NonNull;

import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.StreamInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class YTGetAudios {
    private Disposable currentWorker;

    private onAudiosListener mListener;
    public AtomicBoolean isLoading = new AtomicBoolean();

    public void getAudios(int serviceId, String url, onAudiosListener listener) {
        mListener = listener;
        if (currentWorker != null) currentWorker.dispose();
        currentWorker = ExtractorHelper.getStreamInfo(serviceId, url, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((@NonNull StreamInfo result) -> {
                    isLoading.set(false);
                    StreamInfo currentInfo = result;
                    List<AudioStream> audioStreams = currentInfo.getAudioStreams();

                    final Callable<List<YTAudioBean>> fetchAndSet = () -> {
                        List<YTAudioBean> list = new ArrayList<>();
                        try {
                            for (AudioStream stream : audioStreams) {
                                final long contentLength = DownloaderImpl.getInstance().getContentLength(stream.getUrl());
                                YTAudioBean temp = new YTAudioBean();
                                temp.url = stream.getUrl();
                                temp.quality = stream.getAverageBitrate() > 0 ? stream.getAverageBitrate() + "kbps" : stream.getFormat().getName();
                                temp.size = formatBytes(contentLength);
                                temp.format = stream.getFormat().getName();
                                list.add(temp);
                            }
                        } catch (Throwable e) {
                            int test = 0;
                        }


                        return list;
                    };


                    Single.fromCallable(fetchAndSet)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::dealsize, this::onError);

                }, (@NonNull Throwable throwable) -> {
                    isLoading.set(false);
                    onError(throwable);
                });

    }


    private void dealsize(@NonNull List<YTAudioBean> list) {
        if (null != mListener) {
            mListener.onSuccess(list);
        }

    }

    private void onError(Throwable exception) {
        if (null != mListener) {
            mListener.onError();
        }
    }

    public static String formatBytes(long bytes) {
        if (bytes <= 0) {
            return "[Unknown]";
        }
        if (bytes < 1024) {
            return String.format("%d B", bytes);
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f kB", bytes / 1024d);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / 1024d / 1024d);
        } else {
            return String.format("%.2f GB", bytes / 1024d / 1024d / 1024d);
        }
    }

    public interface onAudiosListener {
        void onSuccess(List<YTAudioBean> list);

        void onError();
    }
}
