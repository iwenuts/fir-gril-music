package music.mp3.song.app.song.music.tube.network;

import android.os.Build;

import encrypt.pck.JiaMiEncrypted;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    private static final String UA = "User-Agent";

    private static String getUA() {
        return Build.BRAND + "/" + Build.MODEL + "/" + Build.VERSION.RELEASE;
    }

    public static <S> S createConfigService(Class<S> serviceClass) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(ApiServiceManager.sCheckProxy)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(JiaMiEncrypted.configBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient).build();

        return retrofit.create(serviceClass);
    }

}
