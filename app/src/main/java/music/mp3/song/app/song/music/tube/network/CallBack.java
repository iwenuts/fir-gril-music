package music.mp3.song.app.song.music.tube.network;

public interface CallBack<T>  {
    void onFail();
    void onSuccess(T t);
}
