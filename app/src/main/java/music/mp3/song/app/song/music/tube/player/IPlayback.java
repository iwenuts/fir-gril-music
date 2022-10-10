package music.mp3.song.app.song.music.tube.player;

import androidx.annotation.Nullable;

import music.mp3.song.app.song.music.tube.bean.Music;


public interface IPlayback {

    void setPlayList(PlayList list);

    boolean play();

    boolean play(PlayList list);

    boolean play(PlayList list, int startIndex);

    boolean play(Music song);

    boolean playLast();

    boolean playNext();

    boolean pause();

    boolean isPlaying();

    long getProgress();

    long getDuration();

    Music getPlayingSong();

    boolean seekTo(int progress);

    void setPlayMode(PlayMode playMode);

    void registerCallback(Callback callback);

    void unregisterCallback(Callback callback);

    void removeCallbacks();

    void releasePlayer();

    interface Callback {

        void onSwitchLast(@Nullable Music last);

        void onSwitchNext(@Nullable Music next);

        void onComplete(@Nullable Music next);

        void onPlayStatusChanged(boolean isPlaying);

        void onLoading(boolean isLoading);
    }
}
