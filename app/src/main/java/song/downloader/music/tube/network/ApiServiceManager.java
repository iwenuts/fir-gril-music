package song.downloader.music.tube.network;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;

import song.downloader.music.tube.MusicApp;
import song.downloader.music.tube.BuildConfig;

import com.zhouyou.http.cookie.CookieManger;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceManager {

    private static ApiServiceManager serviceManager;
    private Retrofit retrofit;

    public static final Interceptor sCheckProxy = chain -> {
        Request request = chain.request();
        if (isUseProxy()) {
            return new Response.Builder()
                    .protocol(Protocol.HTTP_1_1)
                    .code(400).request(request)
                    .message("")
                    .body(ResponseBody.create(MediaType.parse("text/plain"), "error"))
                    .build();
        }
        return chain.proceed(request);
    };

    private static String getSha1(String str) {

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes(Charset.forName("UTF-8")));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getJamCall(String url) {
        double salt = Math.random();

        return "$" + getSha1(url + salt) + "*" + salt + "~";
    }

    private static Interceptor sKEY = chain -> {
        Request request = chain.request();

        HttpUrl url = request.url();
        if (ApiConstants.JAM_URL.contains(url.host())) {
            int version = MusicApp.config.jamAppVersion;
            String path = url.encodedPath();
            request = request.newBuilder().header("user-agent",
                    getUserAgent())
                    .header("x-jam-call", getJamCall(path))
                    .header("x-requested-with", "com.jamendo")
                    .header("x-jam-version", Integer.toString(version, 36))
                    .header("sec-fetch-site", "cross-site")
                    .header("sec-fetch-mode", "cors")
                    .build();
            return chain.proceed(request);
        }
//        if (url.host().contains("soundcloud")) {
//            url = url.newBuilder()
//                    .addQueryParameter("client_id", App.config.s)
//                    .build();
//            request = request.newBuilder().url(url).build();
//            return chain.proceed(request);
//        }

        return chain.proceed(request);
    };

    public static String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(MusicApp.sContext);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        try {
            StringBuffer sb = new StringBuffer();
            for (int i = 0, length = userAgent.length(); i < length; i++) {
                char c = userAgent.charAt(i);
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", (int) c));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A403 Safari/8536.25";
        }
    }

    private ApiServiceManager() {

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(sCheckProxy)
                .addInterceptor(sKEY)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .cookieJar(new CookieMgr())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.JAM_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient).build();
    }

    private class CookieMgr implements CookieJar {
        private CookieJar impl;

        private CookieMgr() {
            Context context = MusicApp.sContext;
            if (context == null) {
                context = MusicApp.getInstance();
            }
            impl = new CookieManger(context);
        }

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (url.toString().contains("jamendo")) {
                impl.saveFromResponse(url, cookies);
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            if (url.toString().contains("jamendo")) {
                return impl.loadForRequest(url);
            }
            return Collections.emptyList();
        }
    }

    public static ApiServiceManager getInstance() {
        if (serviceManager == null) {
            serviceManager = new ApiServiceManager();
        }
        return serviceManager;
    }

    private Map<Class<?>, Object> mApiMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        T api = (T) mApiMap.get(clazz);
        if (api == null) {
            api = retrofit.create(clazz);
            mApiMap.put(clazz, api);
        }
        return api;
    }

    public JamMusicApi getMusicApi() {
        return get(JamMusicApi.class);
    }

    public static boolean isUseProxy() {
        if (BuildConfig.DEBUG) {
            return false;
        }
        String httpProxyHost = System.getProperty("http.proxyHost");
        String httpProxyPort = System.getProperty("http.proxyPort");
        int httpPort = Integer.parseInt(httpProxyPort == null ? "-1" : httpProxyPort);

        String httpsProxyHost = System.getProperty("https.proxyHost");
        String httpsProxyPort = System.getProperty("https.proxyPort");
        int httpsPort = Integer.parseInt(httpsProxyPort == null ? "-1" : httpsProxyPort);

        return (!TextUtils.isEmpty(httpProxyHost) && httpPort > 0)
                || (!TextUtils.isEmpty(httpsProxyHost) && httpsPort > 0);
    }
}
