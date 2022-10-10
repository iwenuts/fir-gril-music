package music.mp3.song.app.song.music.tube.bean;


public class ZlDownloadEvent extends BaseBean {
    /**
     * 0: unstart
     * 1: downloading
     * 2: finished
     * 3:error
     */
    public int status;

    public int progress;

    public Music bean;

    public int soBytes;

}
