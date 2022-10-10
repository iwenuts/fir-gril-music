package music.mp3.song.app.song.music.tube.player;

import androidx.annotation.NonNull;

import music.mp3.song.app.song.music.tube.bean.BaseBean;
import music.mp3.song.app.song.music.tube.bean.Music;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PlayList extends BaseBean {
    public int playingIndex = 0;
    public List<Music> songs = new ArrayList<>();


    public static final int NO_POSITION = -1;
    private PlayMode playMode = PlayMode.LOOP;

    public boolean prepare() {
        if (null == songs || songs.isEmpty()) return false;
        if (playingIndex == NO_POSITION) {
            playingIndex = 0;
        }
        return true;
    }

    public Music getCurrentSong() {
        if (playingIndex != NO_POSITION) {
            if (songs==null || songs.size()==0||songs.size()<=playingIndex){
                return null;
            }
            return songs.get(playingIndex);
        }
        return null;
    }
    public int getNumOfSongs() {
        return null==songs?0:songs.size();
    }

    public int getPlayingIndex() {
        return playingIndex;
    }

    public void setPlayingIndex(int playingIndex) {
        this.playingIndex = playingIndex;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    @NonNull
    public List<Music> getSongs() {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        return songs;
    }

    public boolean hasLast() {
        return songs != null && songs.size() != 0;
    }

    public Music last() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex - 1;
                if (newIndex < 0) {
                    newIndex = songs.size() - 1;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    /**
     * @return Whether has next song to play.
     * <p/>
     * If this query satisfies these conditions
     * - comes from media player's complete listener
     * - current play mode is PlayMode.LIST (the only limited play mode)
     * - current song is already in the end of the list
     * then there shouldn't be a next song to play, for this condition, it returns false.
     * <p/>
     * If this query is from user's action, such as from play controls, there should always
     * has a next song to play, for this condition, it returns true.
     */
    public boolean hasNext(boolean fromComplete) {
        if (songs.isEmpty()) return false;
        if (fromComplete) {
            if (playMode == PlayMode.LIST && playingIndex + 1 >= songs.size()) return false;
        }
        return true;
    }

    /**
     * Move the playingIndex forward depends on the play mode
     *
     * @return The next song to play
     */
    public Music next() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex + 1;
                if (newIndex >= songs.size()) {
                    newIndex = 0;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    private int randomPlayIndex() {
        int randomIndex = new Random().nextInt(songs.size());
        // Make sure not play the same song twice if there are at least 2 songs
        if (songs.size() > 1 && randomIndex == playingIndex) {
            randomPlayIndex();
        }
        return randomIndex;
    }
}
