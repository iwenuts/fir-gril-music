package music.mp3.song.app.song.music.tube.network;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import music.mp3.song.app.song.music.tube.MusicApp;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CountryApi {

    private final OkHttpClient mClient;

    public CountryApi() {
        mClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {

                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();
                        request = request.newBuilder()
                                .addHeader("User-Agent", WebSettings.getDefaultUserAgent(MusicApp.sContext))
                                .build();
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .proxy(Proxy.NO_PROXY)
                .build();
    }

    public interface BlockCallback {
        void onBlockResult(boolean block);
    }

    public void checkBlock(BlockCallback b) {
        String block_cn = MusicApp.config.ban;
        if (TextUtils.isEmpty(block_cn)) {
            if (b != null) {
                b.onBlockResult(false);
            }
            return;
        }
        String block_cns = block_cn.toLowerCase();
        Disposable subscribe = Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean block = getCountryCodeMethod1(block_cns);
                if (block) {
                    return true;
                }
                block = getCountryCodeMethod2(block_cns);
                return block;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (b != null) {
                            b.onBlockResult(aBoolean);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if (b != null) {
                            b.onBlockResult(false);
                        }
                    }
                });
    }

    private Response rawGet(String url) throws Throwable {
        Request request = new Request.Builder()
                .url("http://ip-api.com/json/")
                .build();
        Call call = mClient.newCall(request);
        return call.execute();
    }

    private boolean getCountryCodeMethod2(String block_cn) {
        try {
            Response response = rawGet("http://ip-api.com/json/");
            String jsonStr = response.body().string();
            JSONObject json = new JSONObject(jsonStr);
            String cn = json.optString("countryCode", "").toLowerCase();
            Log.d("Country", String.format("client country code: %s", cn));
            FlurryEventReport.sendIpCode2(cn);
            if (block_cn.contains(cn)) {
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean getCountryCodeMethod1(String block_cn) {
        try {
            Response response = rawGet("https://api.ipify.org/");
            String ip = response.body().string().trim();
            Log.d("Country", String.format("client ip: %s", ip));
            String url = String.format("https://ipapi.co/%s/json/", ip);
            response = rawGet(url);
            String jsonStr = response.body().string();
            JSONObject json = new JSONObject(jsonStr);
            String cn = json.optString("country_code", "").toLowerCase();
            Log.d("Country", String.format("client country code: %s", cn));
            FlurryEventReport.sendIpCode1(cn);
            if (block_cn.contains(cn)) {
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
