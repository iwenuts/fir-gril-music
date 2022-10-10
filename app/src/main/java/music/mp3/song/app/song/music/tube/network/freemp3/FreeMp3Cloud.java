package music.mp3.song.app.song.music.tube.network.freemp3;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.network.CallBack;


public class FreeMp3Cloud {
    private static FreeMp3Cloud mFreeMp3Cloud = new FreeMp3Cloud();
    private String url = "https://www.freemp3cloud.com/downloader";
    private String __RequestVerificationToken = "";
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static FreeMp3Cloud getInstance() {
        return mFreeMp3Cloud;
    }

    private FreeMp3Cloud() {

    }

    public static void init() {
        getInstance().resolveToken();
    }

    private void resolveToken() {
        if (!TextUtils.isEmpty(__RequestVerificationToken)) {
            return;
        }
        FreeMp3Http.initFreemp3(new CallBack<String>() {
            @Override
            public void onFail() {

            }

            @Override
            public void onSuccess(String s) {
                __RequestVerificationToken = s;
            }
        });
    }

    private <T> void searchSong(String word, SearchListener listener) {
        if (TextUtils.isEmpty(__RequestVerificationToken)) {
            listener.onError(false);
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("searchSong", word);
        map.put("__RequestVerificationToken", __RequestVerificationToken);
        if (!TextUtils.isEmpty(MusicApp.config.freemp3url)) {
            url = MusicApp.config.freemp3url;
        }

        FreeMp3Http.post(url, map, new CallBack<String>() {
            @Override
            public void onFail() {
                mHandler.post(() -> listener.onError(false));
            }

            @Override
            public void onSuccess(String html) {
                Document document = Jsoup.parse(html);
                Element bodyElement = document.body();
                Element wrapElement = bodyElement.getElementsByClass("wrap").first();
                Element resultElement = wrapElement.getElementsByClass("s-results").first();
                Elements playItems = resultElement.getElementsByClass("play-item");
                List<Music> musicBeans = new ArrayList<>();
                for (Element playItem : playItems) {
                    Element artistElement = playItem.getElementsByClass("s-artist").first();
                    Element titleElement = playItem.getElementsByClass("s-title").first();
                    Element timeElement = playItem.getElementsByClass("s-time-hq").first();

                    Element playElement = playItem.getElementsByClass("play-ctrl").first();
//                    Element downElement = playItem.getElementsByClass("downl").first();

                    String artist = artistElement.text();
                    String title = titleElement.text();
                    String time = timeElement.text();
                    String play = playElement.attr("data-src");

                    Music bean = new Music();
                    bean.artistName = artist;
                    bean.id = play.hashCode() + "";
                    bean.listenUrl = play;
                    bean.duration = time;
                    bean.downloadUrl = play;
                    bean.title = title;
                    bean.channel = Music.CHANNEL_FREE_MP3;

                    musicBeans.add(bean);
                }
                mHandler.post(() -> {
                    if (musicBeans.isEmpty())
                        listener.onError(true);
                    else
                        listener.onSuccess(musicBeans);
                });

            }
        });


    }

    public static <T> void search(String word, SearchListener searchListener) {
        getInstance().searchSong(word, searchListener);
    }


    public interface SearchListener {
        void onError(boolean empty);

        void onSuccess(List<Music> list);
    }


}
