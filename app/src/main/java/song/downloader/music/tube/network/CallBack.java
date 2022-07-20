package song.downloader.music.tube.network;

public interface CallBack<T>  {
    void onFail();
    void onSuccess(T t);
}
