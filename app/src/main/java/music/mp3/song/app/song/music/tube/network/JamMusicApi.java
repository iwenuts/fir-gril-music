package music.mp3.song.app.song.music.tube.network;


import music.mp3.song.app.song.music.tube.bean.jm.JamArtist;
import music.mp3.song.app.song.music.tube.bean.jm.JamTag;
import music.mp3.song.app.song.music.tube.bean.jm.JamTrack;
import music.mp3.song.app.song.music.tube.bean.jm.JamUp;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JamMusicApi {

    @GET("/api/update")
    Observable<List<JamUp>> update();

    @GET("/api/tags?order=featureRank&limit=40&lang=en&category[]=genre")
    Observable<List<JamTag>> tags();

    @GET("/api/tracks")
    Call<List<JamTrack>> getTracksByTag(@Query("order") String order, @Query("tagId") long tagId,
                                        @Query("limit") int limit, @Query("offset") int offset);

//    @GET("/api/search?identities=www&type=track")
    @GET("/api/search")
    Call<List<JamTrack>> search(@Query("query") String query, @Query("type") String type, @Query("limit") int limit,
                                @Query("identities") String identities);

    @GET("/api/tracks")
    Call<List<JamTrack>> getPopular(@Query("order") String order, @Query("limit") int limit,
                                    @Query("offset") int offset);

    @GET("/api/artists")
    Call<List<JamArtist>> artist(@Query("id[]") List<Long> ids);

}
