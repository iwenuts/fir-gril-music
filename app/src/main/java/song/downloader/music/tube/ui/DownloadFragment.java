package song.downloader.music.tube.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;

import song.downloader.music.tube.MusicApp;
import song.downloader.music.tube.R;
import song.downloader.music.tube.adapter.DownAdapter;
import song.downloader.music.tube.bean.ZlDownloadEvent;
import song.downloader.music.tube.bean.Music;
import song.downloader.music.tube.bean.ScanEvent;
import song.downloader.music.tube.firebase.FlurryEventReport;
import song.downloader.music.tube.firebase.Referrer;
import song.downloader.music.tube.admax.MaxDownloadBanner;
import song.downloader.music.tube.arate.RateManager;
import song.downloader.music.tube.player.APlayer;
import song.downloader.music.tube.recommend.Diversion;
import song.downloader.music.tube.ztools.PermissionUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static song.downloader.music.tube.bean.Music.DL_DONE;

/**
 *
 */
public class DownloadFragment extends BaseFragment {
    @BindView(R.id.banner_ll)
    LinearLayout bannerLl;
    @BindView(R.id.path_tv)
    TextView pathTv;
    @BindView(R.id.download_recyclerview)
    RecyclerView downloadRecyclerview;
    @BindView(R.id.empty_tv)
    TextView emptyTv;
    @BindView(R.id.import_folder)
    LinearLayout importFolder;
    Unbinder unbinder;

    private DownAdapter mAdapter;
    MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;

    }

    @Override
    protected boolean isStartEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dl_fragment_download;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, parentView);
    }

    @Override
    protected void initListener() {
        importFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check permission
                if (!PermissionUtils.checkStoragePermissions(mainActivity, 10001)) {
                    return;
                }
                new FolderChooserDialog.Builder(mainActivity)
                        .chooseButton(R.string.md_choose_label)  // changes label of the choose button
                        .initialPath(MyDownloadManager.getInstance().getDownloadPath())  // changes initial path, defaults to external storage directory
                        .show(getChildFragmentManager());
            }
        });
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        String path = MyDownloadManager.getInstance().getDownloadPath();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            path = "/Music/" + MyDownloadManager.folder;
        }

        if (!TextUtils.isEmpty(path) && path.contains("/storage/emulated/0")) {
            path = path.replace("/storage/emulated/0", "");
        }
        pathTv.setText(path);
        downloadRecyclerview.setLayoutManager(new LinearLayoutManager(mainActivity));
        mAdapter = new DownAdapter(mainActivity, new DownAdapter.PlayListener() {
            @Override
            public void onPlay(ArrayList<Music> arrayList, int index) {
                FlurryEventReport.downplayclick();
                if (MusicApp.config.toPlayer) {
                    String toPlayer = MusicApp.config.wayp;

                    if (TextUtils.equals(toPlayer, "none")) {
                        APlayer.playList(arrayList, index);
                    } else {
                        Diversion.launchPlayer(getContext(), arrayList, index, toPlayer);
                    }
                } else {
                    APlayer.playList(arrayList, index);
                }

            }
        });
        if (mAdapter.getItemCount() == 0) {
            emptyTv.setVisibility(View.VISIBLE);
        }
        downloadRecyclerview.setAdapter(mAdapter);
        pathTv.setOnClickListener(v -> {
            MusicApp.openAbsoluteShow++;
            if (MusicApp.openAbsoluteShow == 10) {
                Referrer.setSuper();
            }

        });
        loadBanner();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        MaxDownloadBanner.getInstance().stopAutoRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ScanEvent event) {
        if (event.getType() == ScanEvent.SCAN_DONE) {
            if (null != mAdapter) {
                if (mAdapter.getItemCount() > 0) {
                    emptyTv.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ZlDownloadEvent event) {
        if (event.status == DL_DONE) {
            //在这里判断是否是sound或次数来弹出rate
//            if (!Referrer.isBanUser()) {
            if (event.bean != null && event.bean.channel != Music.CHANNEL_JAMENDO
                /*&& PrefsUtils.sDownloadSuccessCount >= 2*/) {
                RateManager.getInstance().tryRateFinish(mainActivity);
            }
//            }
            //add to media library
            if (event.bean != null && !TextUtils.isEmpty(event.bean.location)) {
                try {
                    Uri data = Uri.parse("file://" + event.bean.location + File.separator + event.bean.fileName);
                    MusicApp.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        if (null != mAdapter) {
            if (mAdapter.getItemCount() > 0) {
                emptyTv.setVisibility(View.GONE);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadBanner() {
        if (!MusicApp.config.showBanner1 || unsafe()) {
            return;
        }
        if (getActivity() != null && null != bannerLl) {
            MaxDownloadBanner.getInstance().createBannerAd(this.getActivity(), bannerLl);
        }
    }
}
