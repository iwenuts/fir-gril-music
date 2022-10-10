package music.mp3.song.app.song.music.tube.network;


import music.mp3.song.app.song.music.tube.ztools.Config;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IApiService {
    @GET("music2.json")
    Call<Config> getConfig();

}
