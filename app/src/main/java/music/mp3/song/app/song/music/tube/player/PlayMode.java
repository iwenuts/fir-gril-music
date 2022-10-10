package music.mp3.song.app.song.music.tube.player;

public enum PlayMode {
    SINGLE,
    LOOP,
    LIST,
    SHUFFLE;

    public static PlayMode getDefault() {
        return LOOP;
    }

    public static PlayMode switchNextMode(PlayMode current) {
        if (current == null) return getDefault();

        switch (current) {
            case LOOP:
                return SHUFFLE;
            case SHUFFLE:
                return SINGLE;
            case SINGLE:
                return LOOP;
        }
        return getDefault();
    }
}
