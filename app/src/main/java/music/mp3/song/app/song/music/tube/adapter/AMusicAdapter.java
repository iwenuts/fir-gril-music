package music.mp3.song.app.song.music.tube.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.cyl.musicapi.BaseApiImpl;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.dialog.QualityDialog;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.arate.RateManager;
import music.mp3.song.app.song.music.tube.network.CallBack;
import music.mp3.song.app.song.music.tube.network.Mp3Juice;
import music.mp3.song.app.song.music.tube.network.nhac.NhacMusic;
import music.mp3.song.app.song.music.tube.network.yt.YTAudioBean;
import music.mp3.song.app.song.music.tube.network.yt.YTGetAudios;
import music.mp3.song.app.song.music.tube.network.yt.YTManager;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.admax.MaxRewardedAds;
import music.mp3.song.app.song.music.tube.ui.MainActivity;
import music.mp3.song.app.song.music.tube.ui.MyDownloadManager;
import music.mp3.song.app.song.music.tube.player.APlayer;
import music.mp3.song.app.song.music.tube.ztools.AdRewardedDialog;
import music.mp3.song.app.song.music.tube.ztools.ImageHelper;
import music.mp3.song.app.song.music.tube.ztools.ShareUtils;
import music.mp3.song.app.song.music.tube.ztools.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;

public class AMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Music> mDatas;
    private Activity mActivity;
    private BindCallback bindCallback;

    public AMusicAdapter(Activity activity, List<Music> datas) {
        mActivity = activity;
        mDatas = datas;
    }

    public void setDatas(List<Music> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void addDatas(List<Music> datas) {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public List<Music> getDatas() {
        return mDatas;
    }

    public void setBindCallback(BindCallback bindCallback) {
        this.bindCallback = bindCallback;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_music, parent, false);
        return new VideoSmallHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (bindCallback != null) {
            bindCallback.onBindAt(position);
        }
        if (holder instanceof VideoSmallHolder) {
            final Music track = (Music) mDatas.get(position);
            final VideoSmallHolder viewHolder = (VideoSmallHolder) holder;
            viewHolder.titleTv.setText(track.getTitle());
            viewHolder.artistTv.setText(track.getArtistName());
            if (!TextUtils.isEmpty(track.getImage())) {
                ImageHelper.loadMusic(viewHolder.imageIv, track.getImage(), mActivity, 60, 60);
            }
//            if (!TextUtils.isEmpty(track.getDuration())) {
//                viewHolder.durationTv.setText(String.valueOf(track.getDuration()));
//                viewHolder.durationLy.setVisibility(View.VISIBLE);
//            } else {
//                viewHolder.durationLy.setVisibility(View.GONE);
//            }
            viewHolder.downloadIv.setTag(track);
            viewHolder.downloadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Music bean = (Music) v.getTag();
                    tryDownload(mActivity, bean);
                }
            });

            View.OnClickListener playClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Music bean = (Music) v.getTag();
                    int postion = (int) v.getTag(R.string.app_name);
                    if (bean.channel == Music.CHANNEL_YT || bean.channel == Music.CHANNEL_SOUND) {
                        if (TextUtils.isEmpty(bean.getDownloadUrl())) {
                            handleYT_SoundUrl(mActivity, bean, true);
                        } else {
                            APlayer.playSingle(bean);
                        }
                    } else if (bean.channel == Music.CHANNEL_MP3juice) {
                        String id = bean.id;
                        showProcessDialog(mActivity, "Playing");
                        Mp3Juice.getMusicDownUrl(id, new CallBack<String>() {
                            @Override
                            public void onFail() {
                                dismissDialog();
                                ToastUtils.showShortToast("Error");
                            }

                            @Override
                            public void onSuccess(String url) {
                                dismissDialog();
                                doneHandleUrl(mActivity, bean, true, url, url);

                            }
                        });
                    } else if (bean.channel == Music.CHANNEL_QQ) {
                        if (TextUtils.isEmpty(bean.getDownloadUrl())) {
                            String id = bean.id;
                            BaseApiImpl.INSTANCE.getSongUrl("qq", id, 128000, songBean -> {
                                String url = songBean.getData().getUrl();
                                doneHandleUrl(mActivity, bean, true, url, url);
                                return Unit.INSTANCE;
                            }, () -> {
                                return Unit.INSTANCE;
                            });
                        } else {
                            APlayer.playSingle(bean);
                        }
                    } else if (bean.channel == Music.CHANNEL_WYY) {
                        if (TextUtils.isEmpty(bean.getDownloadUrl())) {
                            String id = bean.id;
                            BaseApiImpl.INSTANCE.getSongUrl("netease", id, 128000, songBean -> {
                                String url = songBean.getData().getUrl();
                                doneHandleUrl(mActivity, bean, true, url, url);
                                return Unit.INSTANCE;
                            }, () -> {
                                return Unit.INSTANCE;
                            });

                        } else {
                            APlayer.playSingle(bean);
                        }
                    } else if (bean.channel == Music.CHANNEL_NHAC) {
                        if (TextUtils.isEmpty(bean.getDownloadUrl())) {
                            NhacMusic.getSongInfo(bean.id, new CallBack<String>() {
                                @Override
                                public void onFail() {
                                }

                                @Override
                                public void onSuccess(String s) {
                                    String url = s;
                                    doneHandleUrl(mActivity, bean, true, url, url);
                                }
                            });
                        } else {
                            APlayer.playSingle(bean);
                        }
                    } else {
                        APlayer.playList(mDatas, postion);
                    }
                }
            };

            viewHolder.playIv.setTag(track);
            viewHolder.playIv.setTag(R.string.app_name, position);
            viewHolder.playIv.setOnClickListener(playClick);

            viewHolder.itemView.setTag(track);
            viewHolder.itemView.setTag(R.string.app_name, position);
            viewHolder.itemView.setOnClickListener(playClick);

        }
    }

    public static void tryDownload(Activity activity, Music bean) {
        if (MusicApp.config.ad && !RateManager.getInstance().isShowRate() && MainActivity.notRewardOrRunOut()) {
            MaxRewardedAds.getInstance().tryShowReward(activity, bean, new AdRewardedDialog.OnRewardCallback() {
                @Override
                public void onReward() {
                    doDownload(activity, bean);
                }

                @Override
                public void onError() {
                    doDownload(activity, bean);
                }
            });
            return;
        }
        doDownload(activity, bean);
    }

    private static void doDownload(Activity activity, Music bean) {
        if (bean.channel == Music.CHANNEL_YT || bean.channel == Music.CHANNEL_SOUND) {
            if (TextUtils.isEmpty(bean.getDownloadUrl())) {
                handleYT_SoundUrl(activity, bean, false);
            } else {
                MyDownloadManager.getInstance().download(bean, activity);
            }
        } else if (bean.channel == Music.CHANNEL_QQ) {
            if (TextUtils.isEmpty(bean.getDownloadUrl())) {
                String id = bean.id;
                BaseApiImpl.INSTANCE.getSongUrl("qq", id, 128000, songBean -> {
                    String url = songBean.getData().getUrl();
                    doneHandleUrl(activity, bean, false, url, url);
                    return Unit.INSTANCE;
                }, () -> {
                    return Unit.INSTANCE;
                });
            } else {
                MyDownloadManager.getInstance().download(bean, activity);
            }

        } else if (bean.channel == Music.CHANNEL_WYY) {

            if (TextUtils.isEmpty(bean.getDownloadUrl())) {
                String id = bean.id;
                BaseApiImpl.INSTANCE.getSongUrl("netease", id, 128000, songBean -> {
                    String url = songBean.getData().getUrl();
                    doneHandleUrl(activity, bean, false, url, url);
                    return Unit.INSTANCE;
                }, () -> {
                    return Unit.INSTANCE;
                });
            } else {
                MyDownloadManager.getInstance().download(bean, activity);
            }

        } else if (bean.channel == Music.CHANNEL_NHAC) {
            if (TextUtils.isEmpty(bean.getDownloadUrl())) {
                String id = bean.id;
                NhacMusic.getSongInfo(id, new CallBack<String>() {
                    @Override
                    public void onFail() {

                    }

                    @Override
                    public void onSuccess(String s) {
                        String url = s;
                        doneHandleUrl(activity, bean, false, url, url);
                    }
                });
            } else {
                MyDownloadManager.getInstance().download(bean, activity);
            }

        } else if (bean.channel == Music.CHANNEL_MP3juice) {
            String id = bean.id;
            showProcessDialog(activity, "Downloading");
            Mp3Juice.getMusicDownUrl(id, new CallBack<String>() {
                @Override
                public void onFail() {
                    dismissDialog();
                    ToastUtils.showShortToast("Error");
                }

                @Override
                public void onSuccess(String url) {
                    dismissDialog();
                    doneHandleUrl(activity, bean, false, url, url);
                }
            });
        } else {
            MyDownloadManager.getInstance().download(bean, activity);
        }
    }

    private static ProgressDialog progressDialog;

    private static void showProcessDialog(Activity activity, String title) {
        if (activity == null) {
            return;
        }
        progressDialog = ProgressDialog.show(activity, title,
                "Please wait..", true, false);
    }

    private static void dismissDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        progressDialog = null;
    }

    private static final List<String> whiteFormatList = Arrays.asList("m4a", "mp3", "ogg");

    private static void handleYT_SoundUrl(Activity activity, Music bean, boolean play) {
        showProcessDialog(activity, play ? "Playing" : "Downloading");
        YTGetAudios.onAudiosListener listener = new YTGetAudios.onAudiosListener() {

            @Override
            public void onSuccess(List<YTAudioBean> list) {
                if (list == null || list.isEmpty()) {
                    onError();
                    return;
                }
                YTAudioBean selected = null;
                for (YTAudioBean audio : list) {
                    for (String ext : whiteFormatList) {
                        if (audio.format.toLowerCase().endsWith(ext)) {
                            selected = audio;
                            break;
                        }
                    }
                    if (selected != null) {
                        break;
                    }
                }
                if (selected == null) {
                    onError();
                    return;
                }
                doneHandleUrl(activity, bean, play, selected.url, selected.url);
            }

            @Override
            public void onError() {
                dismissDialog();
                ToastUtils.showShortToast("Error");
            }
        };

        if (bean.channel == Music.CHANNEL_YT) {
            YTManager.getInstance().getAudios(bean.listenUrl, listener);
        } else if (bean.channel == Music.CHANNEL_SOUND) {
            YTManager.getInstance().getAudiosSound(bean.listenUrl, listener);
        }
    }

    private static void doneHandleUrl(Activity activity, Music bean, boolean play, String dUrl, String lUrl) {
        bean.setDownloadUrl(dUrl);
        bean.setListenUrl(lUrl);

        dismissDialog();

        if (play) {
            APlayer.playSingle(bean);
        } else {
            if (activity instanceof AppCompatActivity) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
                QualityDialog qualityDialog = new QualityDialog();
                qualityDialog.setTitle(bean.title);
                qualityDialog.setOnSelectListner(new QualityDialog.OnSelectListner() {
                    @Override
                    public void mOnSelectListner(int index) {
                        MyDownloadManager.getInstance().download(bean, activity);
                    }

                    @Override
                    public void mOnSelectAdListener(String adurl) {
                        if (!TextUtils.isEmpty(adurl)) {
                            if (adurl.startsWith("http")) {
                                ShareUtils.openBrowser(activity, adurl);
                            } else {
                                ShareUtils.gotoGoogePlayStore(activity, adurl);
                            }
                        }
                    }
                });
                qualityDialog.show(appCompatActivity.getSupportFragmentManager());
            } else {
                MyDownloadManager.getInstance().download(bean, activity);
            }


        }
    }

    @Override
    public int getItemCount() {
        return null == mDatas ? 0 : mDatas.size();
    }

    public class VideoSmallHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_iv)
        ImageView imageIv;
        @BindView(R.id.title_tv)
        TextView titleTv;
        @BindView(R.id.artist_tv)
        TextView artistTv;
        @BindView(R.id.duration_ll)
        LinearLayout durationLy;
        @BindView(R.id.duration_tv)
        TextView durationTv;
        @BindView(R.id.download_iv)
        ImageView downloadIv;
        @BindView(R.id.play_iv)
        ImageView playIv;

        public VideoSmallHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface BindCallback {
        void onBindAt(int position);
    }
}
