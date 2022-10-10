package music.mp3.song.app.song.music.tube.player;

import android.net.Uri;

import androidx.annotation.Nullable;

import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.network.ApiServiceManager;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.bean.PlayEvent;
import music.mp3.song.app.song.music.tube.ztools.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class APlayer implements IPlayback {

    public static void playSingle(Music bean) {
        PlayEvent event = new PlayEvent();
        event.list.add(bean);
        EventBus.getDefault().postSticky(event);
        ToastUtils.showShortToast("Playing……");
    }

    public static void playList(List<Music> list, int index) {
        if (list == null || list.isEmpty()) {
            return;
        }
        PlayEvent event = new PlayEvent();
        event.list.addAll(list);
        event.index = index;
        EventBus.getDefault().postSticky(event);
        ToastUtils.showShortToast("Playing……");
    }

    private static final String TAG = "Player";

    private static volatile APlayer sInstance;

    private SimpleExoPlayer mPlayer;

    private PlayList mPlayList;
    // Default size 2: for service and UI
    private List<Callback> mCallbacks = new ArrayList<>(2);

    // Player status
    private boolean isPaused;

    private int lastPlaybackState = ExoPlayer.STATE_IDLE;

    private DataSource.Factory dataSourceFactory;

    private APlayer() {
        dataSourceFactory = new DefaultDataSourceFactory(MusicApp.getInstance(), ApiServiceManager.getUserAgent());

        mPlayer = ExoPlayerFactory.newSimpleInstance(MusicApp.getInstance());
        mPlayer.addAnalyticsListener(new EventLogger(null, "Player-Log"));
        mPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_READY && lastPlaybackState == ExoPlayer.STATE_BUFFERING) {
//                    onBufferingEnd();
                }
                if (playbackState == ExoPlayer.STATE_READY) { // prepared
                    onPrepared();
                } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
//                    onBufferingStart();
                    notifyPlayLoading(true);
                } else if (playbackState == ExoPlayer.STATE_ENDED) {
                    onCompletion();
                }
                lastPlaybackState = playbackState;
            }

            @Override
            public void onPlayerError(ExoPlaybackException e) {
                e.printStackTrace();
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
//                notifyPlayStatusChanged(isPlaying);
            }
        });

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build();

        mPlayer.setAudioAttributes(audioAttributes, true);

        mPlayList = new PlayList();
    }

    private void onPrepared() {
        notifyPlayLoading(false);
        notifyPlayStatusChanged(true);
        mPlayList.getCurrentSong().realduration = mPlayer.getDuration();
    }

    public static APlayer getInstance() {
        if (sInstance == null) {
            synchronized (APlayer.class) {
                if (sInstance == null) {
                    sInstance = new APlayer();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void setPlayList(PlayList list) {
        if (list == null) {
            list = new PlayList();
        }
        mPlayList = list;
    }

    @Override
    public boolean play() {
        if (isPaused) {
            mPlayer.setPlayWhenReady(true);
            notifyPlayStatusChanged(true);
            return true;
        }
        if (mPlayList.prepare()) {
            Music song = mPlayList.getCurrentSong();
            try {
                MediaSource mediaSource;
                mPlayer.stop(true);
                if (song.downloadStats == Music.DL_DONE && !TextUtils.isEmpty(song.location)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (song.location.startsWith("content://")) {
                            boolean isExist = false;
                            try {
                                Uri uri = Uri.parse(song.location);
                                ParcelFileDescriptor fd = MusicApp.getInstance().getContentResolver().openFileDescriptor(uri, "r");
                                fd.close();
                                isExist = true;
                            } catch (Exception e) {
                                isExist = false;
                            }
                            if (isExist) {
                                mediaSource = createSource(song.location);
                            } else {
                                String playUrl = song.getListenUrl();
                                mediaSource = createSource(playUrl);
                            }
                        } else {
                            File f = new File(song.location, song.fileName);
                            if (f.exists()) {
                                mediaSource = createSource(f.getAbsolutePath());
                            } else {
                                String playUrl = song.getListenUrl();
                                mediaSource = createSource(playUrl);
                            }
                        }
                    } else {
                        File f = new File(song.location, song.fileName);
                        if (f.exists()) {
                            mediaSource = createSource(f.getAbsolutePath());
                        } else {
                            String playUrl = song.getListenUrl();
                            mediaSource = createSource(playUrl);
                        }
                    }
                } else {
                    String playUrl = song.getListenUrl();
                    mediaSource = createSource(playUrl);
                }
                notifyPlayLoading(true);
                mPlayer.prepare(mediaSource);
                mPlayer.setPlayWhenReady(true);
            } catch (Throwable e) {
                Log.e(TAG, "play: ", e);
                notifyPlayStatusChanged(false);
                return false;
            }
            return true;
        }
        return false;
    }

    private MediaSource createSource(String pathOrUrl) {
        if (pathOrUrl.toLowerCase().contains("m3u8")) {
            return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(pathOrUrl));
        }
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(pathOrUrl));
    }

    @Override
    public boolean play(PlayList list) {
        if (list == null) return false;

        isPaused = false;
        setPlayList(list);
        return play();
    }

    @Override
    public boolean play(PlayList list, int startIndex) {
        if (list == null || startIndex < 0 || startIndex >= list.getNumOfSongs()) return false;

        isPaused = false;
        list.setPlayingIndex(startIndex);
        setPlayList(list);
        return play();
    }

    @Override
    public boolean play(Music song) {
        if (song == null) return false;

        isPaused = false;
        mPlayList.getSongs().clear();
        mPlayList.getSongs().add(song);
        return play();
    }

    @Override
    public boolean playLast() {
        isPaused = false;
        boolean hasLast = mPlayList.hasLast();
        if (hasLast) {
            Music last = mPlayList.last();
            play();
            notifyPlayLast(last);
            return true;
        }
        return false;
    }

    @Override
    public boolean playNext() {
        isPaused = false;
        boolean hasNext = mPlayList.hasNext(false);
        if (hasNext) {
            Music next = mPlayList.next();
            play();
            notifyPlayNext(next);
            return true;
        }
        return false;
    }

    @Override
    public boolean pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.setPlayWhenReady(false);
            isPaused = true;
            notifyPlayStatusChanged(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPlaying() {
        if (null == mPlayer) {
            return false;
        }
        return mPlayer.isPlaying();
    }

    @Override
    public long getProgress() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    @Nullable
    @Override
    public Music getPlayingSong() {
        return mPlayList.getCurrentSong();
    }

    @Override
    public boolean seekTo(int progress) {
        if (mPlayList.getSongs().isEmpty()) return false;

        Music currentSong = mPlayList.getCurrentSong();
        if (currentSong != null && currentSong.realduration > 0) {
            if (currentSong.realduration <= progress) {
                onCompletion();
            } else {
                mPlayer.seekTo(progress);
            }
            return true;
        }
        return false;
    }

    @Override
    public void setPlayMode(PlayMode playMode) {
        mPlayList.setPlayMode(playMode);
    }

    // Listeners

    private void onCompletion() {
        Music next = null;
        // There is only one limited play mode which is list, player should be stopped when hitting the list end
        if (mPlayList.getPlayMode() == PlayMode.LIST && mPlayList.getPlayingIndex() == mPlayList.getNumOfSongs() - 1) {
            // In the end of the list
            // Do nothing, just deliver the callback
        } else if (mPlayList.getPlayMode() == PlayMode.SINGLE) {
            next = mPlayList.getCurrentSong();
            play();
        } else {
            boolean hasNext = mPlayList.hasNext(true);
            if (hasNext) {
                next = mPlayList.next();
                //add
                notifyPlayNext(next);
                play();
            }
        }
        notifyComplete(next);
    }

    @Override
    public void releasePlayer() {
        mPlayList = null;
        mPlayer.stop();
        mPlayer.stop(true);
        mPlayer.release();
        mPlayer = null;
        sInstance = null;
    }

    // Callbacks

    @Override
    public void registerCallback(Callback callback) {
        mCallbacks.add(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void removeCallbacks() {
        mCallbacks.clear();
    }

    private void notifyPlayStatusChanged(boolean isPlaying) {
        for (Callback callback : mCallbacks) {
            callback.onPlayStatusChanged(isPlaying);
        }
    }

    private void notifyPlayLast(Music song) {
        for (Callback callback : mCallbacks) {
            callback.onSwitchLast(song);
        }
    }

    private void notifyPlayNext(Music song) {
        for (Callback callback : mCallbacks) {
            callback.onSwitchNext(song);
        }
    }

    private void notifyComplete(Music song) {
        for (Callback callback : mCallbacks) {
            callback.onComplete(song);
        }
    }

    private void notifyPlayLoading(boolean isLoading) {
        for (Callback callback : mCallbacks) {
            callback.onLoading(isLoading);
        }
    }
}
