package music.mp3.song.app.song.music.tube.network;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import music.mp3.song.app.song.music.tube.bean.Mp3JuiceBean;
import music.mp3.song.app.song.music.tube.bean.Mp3JuiceMusicDetail;
import music.mp3.song.app.song.music.tube.bean.Music;

public class Mp3Juice {
    private static OkHttpClient mOkHttpClient = new OkHttpClient();
    private static Gson mGson = new Gson();
    private static Handler handler = new Handler(Looper.getMainLooper());


    public static void search(String word, SearchListener listener) {
        String url = "https://mp3-juice.com/api.php?q=" + word;
        Request request = new Request.Builder()
                .url(url)
                .header("Host", "mp3-juice.com")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .header("Referer", "https://mp3-juice.com/")
                .header("sec-ch-ua-mobile", "?0")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
                .header("sec-ch-ua", "\"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"92\"")
                .get()
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(() -> {
                    if (listener != null)
                        listener.onError(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String items = response.body().string();
                    Mp3JuiceBean mp3JuiceBean = mGson.fromJson(items, Mp3JuiceBean.class);
                    List<Mp3JuiceBean.ItemsBean> itemsBeans = mp3JuiceBean.getItems();
                    List<Music> musicBeans = new ArrayList<>();
                    if (itemsBeans != null && !itemsBeans.isEmpty()) {
                        for (Mp3JuiceBean.ItemsBean itemsBean : itemsBeans) {
                            Music bean = new Music();
                            bean.artistName = itemsBean.getChannelTitle();
                            bean.id = itemsBean.getId();
                            bean.duration = itemsBean.getDuration();
                            bean.title = itemsBean.getTitle();
                            bean.image = itemsBean.getThumbHigh();
                            bean.channel = Music.CHANNEL_MP3juice;
                            musicBeans.add(bean);
                        }
                    }
                    handler.post(() -> {
                        if (listener != null)
                            listener.onSuccess(musicBeans);
                    });

                }
            }
        });
    }

    public static void getMusicDownUrl(String id, CallBack<String> callBack) {
        String url = "https://api.mp3yt.link/api/json/mp3/" + id;
        Request request = new Request.Builder()
                .url(url)
                .header("Host", "api.mp3yt.link")
                .header("sec-ch-ua", "\"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"92\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
                .header("Origin", "https://mp3-juice.com")
                .header("Referer", "https://mp3-juice.com/")
                .header("Sec-Fetch-Site", "cross-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .get()
                .build();
        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(() -> {
                    if (callBack != null) {
                        callBack.onFail();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Mp3JuiceMusicDetail mp3JuiceMusicDetail = mGson.fromJson(json, Mp3JuiceMusicDetail.class);
                    if (mp3JuiceMusicDetail.isSuccessful()) {
                        List<Mp3JuiceMusicDetail.VidInfoBean> vidInfoBeans = mp3JuiceMusicDetail.getVidInfo();
                        if (vidInfoBeans != null && !vidInfoBeans.isEmpty()) {
                            Mp3JuiceMusicDetail.VidInfoBean vidInfoBean = vidInfoBeans.get(0);
                            String url = vidInfoBean.getDloadUrl();
                            handler.post(() -> {
                                if (callBack != null) {
                                    callBack.onSuccess(url);
                                }
                            });
                        }
                    }
                }
            }
        });
    }


    public interface SearchListener {
        void onError(boolean empty);

        void onSuccess(List<Music> list);
    }
}
