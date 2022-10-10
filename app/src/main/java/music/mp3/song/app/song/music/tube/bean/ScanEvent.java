package music.mp3.song.app.song.music.tube.bean;


public class ScanEvent extends BaseBean {
    public static final int SCAN_START = 1;
    public static final int SCAN_DONE = 2;

    private int type;
    public ScanEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
