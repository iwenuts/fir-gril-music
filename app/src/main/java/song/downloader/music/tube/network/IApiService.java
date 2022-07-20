package song.downloader.music.tube.network;


import song.downloader.music.tube.ztools.Config;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IApiService {
    @GET("music1.json")
    Call<Config> getConfig();

}
