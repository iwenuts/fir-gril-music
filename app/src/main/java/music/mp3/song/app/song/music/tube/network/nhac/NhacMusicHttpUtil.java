package music.mp3.song.app.song.music.tube.network.nhac;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import music.mp3.song.app.song.music.tube.network.CallBack;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

class NhacMusicHttpUtil {
    private static OkHttpClient okHttpClient;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
           ;
        okHttpClient = builder.build();
    }

    public static <T> void get(String url, CallBack callBack) {
        Request request = new Request.Builder().get().url(url).build();
        launchRequest(request, callBack);
    }

    private static String userAgent = "Mozilla/5.0 (Linux; Android 4.4.2; de-de; SAMSUNG GT-I9195 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Version/1.5 Chrome/28.0.1500.94 Mobile Safari/537.36";

    public static <T> void post(String url, Map<String, String> body, CallBack callBack) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : body.keySet()) {
            builder.add(key, body.get(key));
        }

//        Content-Type: application/x-www-form-urlencoded;charset=UTF-8
//        User-Agent: Mozilla/5.0 (Linux; Android 4.4.2; de-de; SAMSUNG GT-I9195 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Version/1.5 Chrome/28.0.1500.94 Mobile Safari/537.36
//        Connection: close
//        Cookie : NHACVN_API=kv2k0f2e9hjj8kau29n9h4kfu3
//        Content-Length : 43

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().post(requestBody).url(url)
                     .build();
        launchRequest(request, callBack);
    }

    private static void launchRequest(Request request, CallBack callBack) {
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    ResponseBody requestBody = response.body();
                    String string = requestBody.string();
                    callBack.onSuccess(string);
                } else {
                    callBack.onFail();
                }
            }
        });

    }


}
