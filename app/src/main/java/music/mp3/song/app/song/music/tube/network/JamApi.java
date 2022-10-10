package music.mp3.song.app.song.music.tube.network;

import androidx.collection.LongSparseArray;


import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.bean.jm.JamArtist;
import music.mp3.song.app.song.music.tube.bean.jm.JamTrack;
import music.mp3.song.app.song.music.tube.ztools.Timeutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class JamApi {

    private JamMusicApi api = ApiServiceManager.getInstance().getMusicApi();
    private Disposable disposable;
    private static LongSparseArray<String> artistIds = new LongSparseArray<>();

    public void search(String query, int pageSize, JamCallback callback) {
        disposable = Observable.fromCallable(() -> {
            CallBean callBean = new CallBean();

            Response<List<JamTrack>> response = api.search(query, "track", pageSize, "www").execute();

            if (!response.isSuccessful() || response.body() == null) {
                callBean.error = true;
                return callBean;
            }

            List<JamTrack> body = response.body();

            if (body.isEmpty()) {
                callBean.error = false;
                callBean.empty = true;
                return callBean;
            }

            List<Music> list = new ArrayList<>();

            List<Long> ids = new ArrayList<>();

            for (JamTrack track : body) {

                if (track.isUnavailable()) {
                    continue;
                }

                if (artistIds.get(track.artistId) == null) {
                    artistIds.put(track.artistId, null);
                    ids.add(track.artistId);
                }

                Music bean = new Music();

                bean.id = Long.toString(track.id);
                bean.artistId = track.artistId;
                bean.image = track.getCover();
                bean.listenUrl = track.getStreamUrl();
                bean.duration = Timeutils.formatDuration(track.duration);
                bean.downloadUrl = track.getStreamUrl();
                bean.title = track.name;
                bean.channel = Music.CHANNEL_JAMENDO;

                list.add(bean);
            }

            if (list.isEmpty()) {
                callBean.error = false;
                callBean.empty = true;
                return callBean;
            }

            callBean.entities = list;

            if (ids.isEmpty()) {
                for (Music entity : list) {
                    entity.artistName = artistIds.get(entity.artistId);
                }
                return callBean;
            }

            Response<List<JamArtist>> artistRes = api.artist(ids).execute();

            List<JamArtist> artists = artistRes.body();
            if (artistRes.isSuccessful() && artists != null
                    && !artists.isEmpty()) {
                for (JamArtist artist : artists) {
                    artistIds.put(artist.id, artist.name);
                }

                for (Music entity : list) {
                    entity.artistName = artistIds.get(entity.artistId);
                }
            }

            return callBean;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBean -> {
                    if (callBean.error) {
                        if (callback != null) {
                            callback.onError(false);
                        }
                    } else if (callBean.empty) {
                        if (callback != null) {
                            callback.onError(true);
                        }
                    } else {
                        if (callback != null) {
                            callback.onLoadSuc(callBean.entities);
                        }
                    }
                }, e -> {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onError(false);
                    }
                });
    }

    public void popular(int pageSize, int offset, JamCallback callback) {
        tracks(-1, pageSize, offset, callback);
    }

    public void tracksByTag(long tagId, int pageSize, int offset, JamCallback callback) {
        tracks(tagId, pageSize, offset, callback);
    }

    private void tracks(long tagId, int pageSize, int offset, JamCallback callback) {
        disposable = Observable.fromCallable(() -> {
            CallBean callBean = new CallBean();

            Response<List<JamTrack>> response;

            if (tagId > 0) {
                response = api.getTracksByTag("hotness", tagId, pageSize, offset).execute();
            } else {
                response = api.getPopular("hotness", pageSize, offset).execute();
            }

            if (!response.isSuccessful() || response.body() == null) {
                callBean.error = true;
                return callBean;
            }

            List<JamTrack> body = response.body();

            if (body.isEmpty()) {
                callBean.error = false;
                callBean.empty = true;
                return callBean;
            }

            List<Music> list = new ArrayList<>();

            List<Long> ids = new ArrayList<>();

            for (JamTrack track : body) {

                if (track.isUnavailable()) {
                    continue;
                }

                if (artistIds.get(track.artistId) == null) {
                    artistIds.put(track.artistId, null);
                    ids.add(track.artistId);
                }

                Music bean = new Music();

//                bean.ext = track.getExt();

                bean.id = Long.toString(track.id);
                bean.artistId = track.artistId;
                bean.image = track.getCover();
                bean.listenUrl = track.getStreamUrl();
                bean.duration = Timeutils.formatDuration(track.duration);
                bean.downloadUrl = track.getStreamUrl();
                bean.title = track.name;
                bean.channel = Music.CHANNEL_JAMENDO;

                list.add(bean);
            }

            if (list.isEmpty()) {
                callBean.error = false;
                callBean.empty = true;
                return callBean;
            }

            callBean.entities = list;

            if (ids.isEmpty()) {
                for (Music entity : list) {
                    entity.artistName = artistIds.get(entity.artistId);
                }
                return callBean;
            }

            Response<List<JamArtist>> artistRes = api.artist(ids).execute();

            List<JamArtist> artists = artistRes.body();
            if (artistRes.isSuccessful() && artists != null
                    && !artists.isEmpty()) {
                for (JamArtist artist : artists) {
                    artistIds.put(artist.id, artist.name);
                }

                for (Music entity : list) {
                    entity.artistName = artistIds.get(entity.artistId);
                }
            }

            return callBean;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBean -> {
                    if (callBean.error) {
                        if (callback != null) {
                            callback.onError(false);
                        }
                    } else if (callBean.empty) {
                        if (callback != null) {
                            callback.onError(true);
                        }
                    } else {
                        if (callback != null) {
                            if (callBean.entities != null) {
                                Collections.shuffle(callBean.entities);
                            }
                            callback.onLoadSuc(callBean.entities);
                        }
                    }
                }, e -> {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onError(false);
                    }
                });
    }

    public interface JamCallback {
        void onLoadSuc(List<Music> list);

        void onError(boolean empty);
    }

    private static class CallBean {
        private List<Music> entities;

        private boolean error;

        private boolean empty;
    }

    public void destroy() {
        if (disposable != null) {
            disposable.dispose();
        }
    }


}
