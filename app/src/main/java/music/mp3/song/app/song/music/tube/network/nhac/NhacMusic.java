package music.mp3.song.app.song.music.tube.network.nhac;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
 
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import music.mp3.song.app.song.music.tube.network.CallBack;


public class NhacMusic {
    private static String searchUrl = "http://api.nhac.vn/client/search";
    private static String songDefaultUrl = "http://api.nhac.vn/client/song/listen";

    private static Gson mGson = new Gson();
    private static Handler mHandler = new Handler(Looper.getMainLooper());


    public static void search(String keyword, CallBack<List<NhacMusicListBean.DataBean>> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("keyword", keyword);
        map.put("type", "song");
        map.put("limit", "50");
        map.put("offset", "1");
        NhacMusicHttpUtil.post(searchUrl, map, new CallBack<String>() {
            @Override
            public void onFail() {
                mHandler.post(() -> {
                    callBack.onFail();
                });
            }

            @Override
            public void onSuccess(String s) {
                if (!TextUtils.isEmpty(s) && s.contains("\"errorCode\":0")) {
                    try {
                        NhacMusicListBean musicListBean = mGson.fromJson(s, NhacMusicListBean.class);
                        List<NhacMusicListBean.DataBean> list = musicListBean.getData();
                        mHandler.post(() -> {
                            if (list == null) {
                                callBack.onFail();
                                return;
                            }
                            callBack.onSuccess(list);
                        });
                    }catch (Exception e){
                        mHandler.post(() -> {
                            callBack.onFail();
                        });
                    }
                }else {
                    mHandler.post(() -> {
                        callBack.onFail();
                    });
                }
            }
        });
    }

    public static void getSongInfo(String id, CallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("profile_id", "1");
        map.put("id", id);

        NhacMusicHttpUtil.post(songDefaultUrl, map, new CallBack<String>() {
            @Override
            public void onFail() {
                mHandler.post(() -> {
                    callBack.onFail();
                });
            }

            @Override
            public void onSuccess(String string) {
                if (!TextUtils.isEmpty(string) && string.contains("\"errorCode\":0")) {
                    NhacMusicInfoBean musicInfoBean  =    mGson.fromJson(string, NhacMusicInfoBean.class);
                    NhacMusicInfoBean.DataDTO dataDTO = musicInfoBean.getData();
                    String stream_url = dataDTO.getStreaming_url();
                    mHandler.post(() -> {
                        callBack.onSuccess(stream_url);
                    });
                }else {
                    mHandler.post(() -> {
                        callBack.onFail();
                    });
                }

            }
        });
    }


}
