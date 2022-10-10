package music.mp3.song.app.song.music.tube.recommend;

/* compiled from: RecommendEventListener */
public interface RecommendEventListener {
    void closed(int i, RecommendBean recommendBean);

    void clicked(int i, RecommendBean recommendBean);

    void showed(int i, RecommendBean recommendBean);
}
