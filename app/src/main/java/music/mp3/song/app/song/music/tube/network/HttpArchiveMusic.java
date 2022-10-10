package music.mp3.song.app.song.music.tube.network;


import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import music.mp3.song.app.song.music.tube.bean.Music;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HttpArchiveMusic {
    private static CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public static void search(String word, SearchListener searchListener) {
        String url = "https://freemusicarchive.org/search?quicksearch=" + word + "&pageSize=200";
        Observable.create(new ObservableOnSubscribe<List<Music>>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<List<Music>> emitter) throws Exception {
                Document document = Jsoup.connect(url).get();
                Element element = document.body();
                Element contentElement = element.getElementById("content");
                Elements colsetCElements = contentElement.getElementsByClass("colset-c");
                Element colsetCElement = colsetCElements.first();
                Elements listElements = colsetCElement.getElementsByClass("box-stnd-10pad-20marg play-lrg-list");
                Element listElement = listElements.first();
                Element playListElement = listElement.getElementsByClass("playlist playlist-lrg").first();
                Elements listItemElements = playListElement.getElementsByClass("play-item gcol gid-electronic");
                List<Music> musicBeans = new ArrayList<>();
                for (Element itemElement : listItemElements) {
                    Element artistElement = itemElement.getElementsByClass("ptxt-artist").first();
                    String artist = artistElement.text();

                    Element trackElement = itemElement.getElementsByClass("ptxt-track").first();
                    String track = trackElement.text();

                    Element downloadElement = itemElement.getElementsByClass("icn-arrow js-download").first();
                    String dataUrl = downloadElement.attr("data-url");
                    dataUrl = dataUrl.substring(0, dataUrl.lastIndexOf("Overlay"));

                    Music bean = new Music();
                    bean.channel = Music.CHANNEL_ARCHIVE;
                    bean.id = dataUrl.hashCode()+"";
                    bean.setTitle(artist);
                    bean.setArtistName(track);
                    bean.setDownloadUrl(dataUrl);
                    bean.setListenUrl(dataUrl);
                    musicBeans.add(bean);
                }
                emitter.onNext(musicBeans);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Music>>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {
                mCompositeDisposable.add(d);
            }

            @Override
            public void onNext(@NotNull List<Music> list) {
                searchListener.onSuccess(list);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                searchListener.onError(true);
            }

            @Override
            public void onComplete() {
                mCompositeDisposable.clear();
            }
        });

    }

    public interface SearchListener {
        void onError(boolean empty);

        void onSuccess(List<Music> list);
    }


}
