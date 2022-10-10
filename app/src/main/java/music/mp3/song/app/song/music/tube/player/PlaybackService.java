package music.mp3.song.app.song.music.tube.player;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.widget.RemoteViews;

import music.mp3.song.app.song.music.tube.bean.Music;


public class PlaybackService extends Service implements IPlayback, IPlayback.Callback {

    private static final String ACTION_PLAY_TOGGLE = "music.mp3.song.app.song.music.tube.ACTION.PLAY_TOGGLE";
    private static final String ACTION_PLAY_LAST = "music.mp3.song.app.song.music.tube.ACTION.PLAY_LAST";
    private static final String ACTION_PLAY_NEXT = "music.mp3.song.app.song.music.tube.ACTION.PLAY_NEXT";
    private static final String ACTION_STOP_SERVICE = "music.mp3.song.app.song.music.tube.ACTION.STOP_SERVICE";

    private static final int NOTIFICATION_ID = 1;

    private RemoteViews mContentViewBig, mContentViewSmall;

    private APlayer mPlayer;

    private final Binder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = APlayer.getInstance();
        mPlayer.registerCallback(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PLAY_TOGGLE.equals(action)) {
                if (isPlaying()) {
                    pause();
                } else {
                    play();
                }
            } else if (ACTION_PLAY_NEXT.equals(action)) {
                playNext();
            } else if (ACTION_PLAY_LAST.equals(action)) {
                playLast();
            } else if (ACTION_STOP_SERVICE.equals(action)) {
                if (isPlaying()) {
                    pause();
                }
                stopForeground(true);
                unregisterCallback(this);
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean stopService(Intent name) {
        stopForeground(true);
        unregisterCallback(this);
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    @Override
    public void setPlayList(PlayList list) {
        mPlayer.setPlayList(list);
    }

    @Override
    public boolean play() {
        return mPlayer.play();
    }

    @Override
    public boolean play(PlayList list) {
        return mPlayer.play(list);
    }

    @Override
    public boolean play(PlayList list, int startIndex) {
        return mPlayer.play(list, startIndex);
    }

    @Override
    public boolean play(Music song) {
        return mPlayer.play(song);
    }

    @Override
    public boolean playLast() {
        return mPlayer.playLast();
    }

    @Override
    public boolean playNext() {
        return mPlayer.playNext();
    }

    @Override
    public boolean pause() {
        return mPlayer.pause();
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
        return mPlayer.getProgress();
    }

    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public Music getPlayingSong() {
        return mPlayer.getPlayingSong();
    }

    @Override
    public boolean seekTo(int progress) {
        return mPlayer.seekTo(progress);
    }

    @Override
    public void setPlayMode(PlayMode playMode) {
        mPlayer.setPlayMode(playMode);
    }

    @Override
    public void registerCallback(Callback callback) {
        mPlayer.registerCallback(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mPlayer.unregisterCallback(callback);
    }

    @Override
    public void removeCallbacks() {
        mPlayer.removeCallbacks();
    }

    @Override
    public void releasePlayer() {
        mPlayer.releasePlayer();
        super.onDestroy();
    }

    // Playback Callbacks

    @Override
    public void onSwitchLast(@Nullable Music last) {
        showNotification();
    }

    @Override
    public void onSwitchNext(@Nullable Music next) {
        showNotification();
    }

    @Override
    public void onComplete(@Nullable Music next) {
        showNotification();
    }

    @Override
    public void onLoading(boolean isLoading) {

    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        showNotification();

    }

    // Notification

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
//        // The PendingIntent to launch our activity if the user selects this notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, AlMainActivity.class), 0);
//
//        // Set the info for the views that show in the notification panel.
//        Notification notification = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
//                .setWhen(System.currentTimeMillis())  // the time stamp
//                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
//                .setCustomContentView(getSmallContentView())
//                .setCustomBigContentView(getBigContentView())
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setOngoing(true)
//                .build();
//
//        // Send the notification.
//        try {
//            startForeground(NOTIFICATION_ID, notification);
//        } catch (Throwable e) {
//
//        }

    }

//    private RemoteViews getSmallContentView() {
//        if (mContentViewSmall == null) {
//            mContentViewSmall = new RemoteViews(getPackageName(), R.layout.remote_view_music_player_small);
//            setUpRemoteView(mContentViewSmall);
//        }
//        updateRemoteViews(mContentViewSmall);
//        return mContentViewSmall;
//    }
//
//    private RemoteViews getBigContentView() {
//        if (mContentViewBig == null) {
//            mContentViewBig = new RemoteViews(getPackageName(), R.layout.remote_view_music_player);
//            setUpRemoteView(mContentViewBig);
//        }
//        updateRemoteViews(mContentViewBig);
//        return mContentViewBig;
//    }
//
//    private void setUpRemoteView(RemoteViews remoteView) {
//        remoteView.setImageViewResource(R.id.image_view_close, R.drawable.ic_remote_view_close);
//        remoteView.setImageViewResource(R.id.image_view_play_last, R.drawable.ic_remote_view_play_last);
//        remoteView.setImageViewResource(R.id.image_view_play_next, R.drawable.ic_remote_view_play_next);
//
//        remoteView.setOnClickPendingIntent(R.id.button_close, getPendingIntent(ACTION_STOP_SERVICE));
//        remoteView.setOnClickPendingIntent(R.id.button_play_last, getPendingIntent(ACTION_PLAY_LAST));
//        remoteView.setOnClickPendingIntent(R.id.button_play_next, getPendingIntent(ACTION_PLAY_NEXT));
//        remoteView.setOnClickPendingIntent(R.id.button_play_toggle, getPendingIntent(ACTION_PLAY_TOGGLE));
//    }
//
//    private void updateRemoteViews(final RemoteViews remoteView) {
//        Music currentSong = mPlayer.getPlayingSong();
//        if (currentSong != null) {
//            remoteView.setTextViewText(R.id.text_view_name, currentSong.getTitle());
//            remoteView.setTextViewText(R.id.text_view_artist, currentSong.getArtistName());
//        }
//        remoteView.setImageViewResource(R.id.image_view_play_toggle, isPlaying()
//                ? R.drawable.ic_remote_view_pause : R.drawable.ic_remote_view_play);
//
//        Picasso.with(this)
//                .load(getPlayingSong().getImage())
//                .resize(50, 50)
//                .centerCrop()
//                .placeholder(R.drawable.icon_default).error(R.drawable.icon_default)
//                .into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        if (bitmap == null) {
//                            remoteView.setImageViewResource(R.id.image_view_album, R.mipmap.ic_launcher);
//                        } else {
//                            remoteView.setImageViewBitmap(R.id.image_view_album, bitmap);
//                        }
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//                    }
//                });
//
//    }
//
//    // PendingIntent
//    private PendingIntent getPendingIntent(String action) {
//        return PendingIntent.getService(this, 0, new Intent(action), 0);
//    }
}
