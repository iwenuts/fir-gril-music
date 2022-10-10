package music.mp3.song.app.song.music.tube.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;

import music.mp3.song.app.song.music.tube.adapter.AMusicAdapter;
import music.mp3.song.app.song.music.tube.widget.BottomNavigationViewHelper;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import music.mp3.song.app.song.music.tube.recommend.RecommendBean;
import music.mp3.song.app.song.music.tube.recommend.RecommendManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.network.ApiConstants;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.bean.MusicSuggistion;
import music.mp3.song.app.song.music.tube.bean.PlayEvent;
import music.mp3.song.app.song.music.tube.bean.ARewardEvent;
import music.mp3.song.app.song.music.tube.bean.ScanEvent;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;
import music.mp3.song.app.song.music.tube.firebase.Referrer;
import music.mp3.song.app.song.music.tube.admax.MaxBackInterstitial;
import music.mp3.song.app.song.music.tube.admax.MaxDownloadInterstitial;
import music.mp3.song.app.song.music.tube.admax.MaxOpenInterstitial;
import music.mp3.song.app.song.music.tube.admax.MaxRewardedAds;
import music.mp3.song.app.song.music.tube.player.PlaybackService;
import music.mp3.song.app.song.music.tube.player.IPlayback;
import music.mp3.song.app.song.music.tube.player.PlayList;
import music.mp3.song.app.song.music.tube.player.PlayMode;
import music.mp3.song.app.song.music.tube.ztools.ImageHelper;
import music.mp3.song.app.song.music.tube.ztools.LogUtil;
import music.mp3.song.app.song.music.tube.ztools.PermissionUtils;
import music.mp3.song.app.song.music.tube.ztools.vPrefsUtils;
import music.mp3.song.app.song.music.tube.ztools.ShareUtils;
import music.mp3.song.app.song.music.tube.ztools.Timeutils;
import music.mp3.song.app.song.music.tube.ztools.ToastUtils;
import music.mp3.song.app.song.music.tube.ztools.Utils;


public class MainActivity extends BaseActivity implements IPlayback.Callback, FolderChooserDialog.FolderCallback {

    private static final String TAG = "AlMainActivity";


    @Override
    public void onFolderChooserDismissed(@NonNull FolderChooserDialog dialog) {
    }

    @BindView(R.id.viewpager)
    ViewPager mainViewpager;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;


    @BindView(R.id.image_iv)
    ImageView imageIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.progress_tv)
    TextView textViewProgress;
    @BindView(R.id.seek_bar)
    AppCompatSeekBar seekBarProgress;
    @BindView(R.id.duration_tv)
    TextView textViewDuration;
    @BindView(R.id.play_mode_toggle)
    ImageView playModeToggle;
    @BindView(R.id.play_or_pause_iv)
    ImageView playOrPauseIv;
    @BindView(R.id.loading_v)
    MaterialProgressBar loadingV;
    @BindView(R.id.last_iv)
    ImageView lastIv;
    @BindView(R.id.next_iv)
    ImageView nextIv;
    @BindView(R.id.download_iv)
    ImageView downloadIv;
    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchView;

    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentPagerAdapter mAdpter;

    public static boolean sIsInActivity;
    public static Activity MA;
    private int mOpenCount;//启动次数

    private boolean isSearched = false;
    private AsyncTask mSearchTask;
    private MenuItem menuItem;

    private IPlayback mPlayer;
    private int mIndex;
    private PlayList mPlayList;
    private Handler mHandler = new Handler();
    ProgressDialog scanDialog;


    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        if (context instanceof Activity) {
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        MA = this;
        ButterKnife.bind(this);
//        if (TextUtils.isEmpty(App.config.promId)) {
        mSearchView.inflateOverflowMenu(R.menu.zl_search_menu);
//        } else {
//            mSearchView.inflateOverflowMenu(R.menu.search_menu2);
//        }

        MaxOpenInterstitial.getInstance().showInterstitial(this);

//        if (BuildConfig.DEBUG) {
//            MyApp.appLovinSdk.showMediationDebugger();
//        }
    }

    @Override
    protected void initListeners() {
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mPlayer != null) {
                    Music currentSong = mPlayer.getPlayingSong();
                    if (null != currentSong) {
                        updateProgressTextWithProgress(progress);
                    } else {
                        seekBarProgress.setProgress(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mProgressCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (null != mPlayer) {
                    Music currentSong = mPlayer.getPlayingSong();
                    if (null != currentSong && null != mPlayer) {
                        seekTo(getDuration(seekBar.getProgress()));
                        if (mPlayer.isPlaying()) {
                            mHandler.removeCallbacks(mProgressCallback);
                            mHandler.post(mProgressCallback);
                        }
                    } else {
                        seekBarProgress.setProgress(0);
                    }
                }
            }
        });
        playModeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null) return;
                PlayMode current = vPrefsUtils.lastPlayMode();
                PlayMode newMode = PlayMode.switchNextMode(current);
                vPrefsUtils.setPlayMode(newMode);
                mPlayer.setPlayMode(newMode);
                updatePlayModeView(newMode);
            }
        });
        playOrPauseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null) return;

                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                } else {
                    mPlayer.play();
                }
            }
        });
        lastIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null)
                    return;
                if (!mPlayer.playLast()) {
                    ToastUtils.showShortToast("No Previous Song");
                }
            }
        });
        nextIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null)
                    return;
                if (!mPlayer.playNext()) {
                    ToastUtils.showShortToast("No Next Song");
                }
            }
        });
        downloadIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null) {
                    return;
                }
                Music bean = mPlayer.getPlayingSong();
                if (bean == null) {
                    return;
                }
                AMusicAdapter.tryDownload(MainActivity.this, bean);
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                onSearchAction(searchSuggestion.getBody());
                mSearchView.clearSearchFocus();
                mSearchView.setSearchText(searchSuggestion.getBody());
            }

            @Override
            public void onSearchAction(String currentQuery) {
                LogUtil.e(TAG, "onSearchAction>>");
                isSearched = true;
                mSearchView.clearSuggestions();
                if (mSearchTask != null) {
                    mSearchTask.cancel(true);
                }
                mSearchView.hideProgress();
                if (TextUtils.isEmpty(currentQuery)) {
                    return;
                }
                SearchActivity.launch(MainActivity.this, currentQuery);
            }
        });
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                leftIcon.setImageResource(R.drawable.ic_search_6060_24dp);
                textView.setText(item.getBody());
            }
        });
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                LogUtil.e(TAG, ">> isSearched " + isSearched);
                if (isSearched) {
                    isSearched = false;
                    return;
                }
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    searchSuggestions(newQuery);
                }
            }
        });
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        try {
                            Intent textIntent = new Intent(Intent.ACTION_SEND);
                            textIntent.setType("text/plain");
                            textIntent.putExtra(Intent.EXTRA_TEXT,
                                    String.format(getString(R.string.share_content), getPackageName()));
                            startActivity(Intent.createChooser(textIntent, getString(R.string.share_text)));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.action_recommend:
                        final RecommendBean recom = RecommendManager.getInstance().getMainRecommend();
                        ShareUtils.gotoRecommend(MainActivity.this, recom != null ? recom.getPackageIdWithRecom() : "");
                        break;
                    case R.id.action_more_apps:
                        ShareUtils.gotoMoreApps(MainActivity.this, MusicApp.config.moreApps);
                        break;
                }
            }
        });
        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                menuItem = item;
                mainViewpager.setCurrentItem(item.getOrder());
                return true;
            }
        });
        mainViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updatePlayModeView(PlayMode playMode) {
        if (playMode == null) {
            playMode = PlayMode.getDefault();
        }
        switch (playMode) {
            case LOOP:
                playModeToggle.setImageResource(R.drawable.al_play_loop);
                break;
            case SHUFFLE:
                playModeToggle.setImageResource(R.drawable.play_shuffle);
                break;
            case SINGLE:
                playModeToggle.setImageResource(R.drawable.play_single);
                break;
        }
    }

    private void searchSuggestions(String newText) {
        if (mSearchTask != null) {
            mSearchTask.cancel(true);
        }

        mSearchTask = new AsyncTask<String, Void, List<MusicSuggistion>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mSearchView != null) {
                    mSearchView.showProgress();
                }
            }

            @Override
            protected List<MusicSuggistion> doInBackground(String... strings) {
                try {
                    LogUtil.v(TAG, "doInBackground suggistion");
                    String query = strings[0];
                    URL url = new URL("http://suggestqueries.google.com/complete/search?client=firefox&hl=fr&q=" + query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, len);
                        }
                        baos.close();
                        is.close();
                        byte[] byteArray = baos.toByteArray();
                        String content = new String(byteArray);
                        LogUtil.v(TAG, "searchSuggestions content::" + content);
                        if (!TextUtils.isEmpty(content)) {
                            JSONArray jsonArray = new JSONArray(content);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray jsonArray1 = jsonArray.optJSONArray(i);
                                if (jsonArray1 != null) {
                                    ArrayList<MusicSuggistion> list = new ArrayList<>();
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        String str = jsonArray1.getString(j);
                                        list.add(new MusicSuggistion(str));
                                    }
                                    return list;
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                mSearchView.hideProgress();
            }

            @Override
            protected void onPostExecute(List<MusicSuggistion> list) {
                super.onPostExecute(list);
                if (list != null && !isFinishing()) {
                    mSearchView.swapSuggestions(list);
                }
                mSearchView.hideProgress();
            }
        }.executeOnExecutor(Utils.sExecutorService, newText);
    }

    @Override
    protected void initDatas() {
        sIsInActivity = true;
        PermissionUtils.checkStoragePermissions(this, 10001);

        //判断是否弹出 update对话框
        try {
            String str = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String version = MusicApp.config.uVer;
            if (version.compareTo(str) > 0) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            new UpdateDialog().showDialog(MainActivity.this, MusicApp.config.uId, MusicApp.config.uForce, MusicApp.config.uInfo);
                        } catch (Exception unused) {
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadAd();

        initViewPages();
        bindService(new Intent(this, PlaybackService.class), mConnection, Context.BIND_AUTO_CREATE);

        //初始化 user
        mOpenCount = vPrefsUtils.getOpenCount();
        mOpenCount++;
        vPrefsUtils.setOpenCount(mOpenCount);
        ApiConstants.sDownloadQuota = 0;
        if (mOpenCount == 1) {
            Timeutils.checkTime();
            ApiConstants.sDownloadQuota = 3;
        }
        if (!vPrefsUtils.getRefererDone()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vPrefsUtils.setReferDone(true);
                }
            }, 3000);
        }
//        AdManager.getInstance().tryShowOpenAd(AlMainActivity.this);

        //放在Timeutils.checkTime()之后
        if (!Referrer.isBanUser()) {
            RecommendManager.getInstance().setProvider(MusicApp.config.promId);
        }
    }

    private void loadAd() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MaxDownloadInterstitial.getInstance().loadInterstitialAd(MainActivity.this);
                MaxBackInterstitial.getInstance().loadBackInterstitialAd(MainActivity.this);
                MaxRewardedAds.getInstance().createRewardedAd(MainActivity.this);
            }
        }, 500);
    }

//    private static boolean gotReward() {
//        return App.config.rewardpop <= 0 ? ApiConstants.rewarded : (ApiConstants.sDownloadQuota >= App.config.rewardpop);
//    }

    public static boolean notRewardOrRunOut() {
        //      return true;
        return MusicApp.config.rewardpop <= 0 ? !ApiConstants.rewarded : (ApiConstants.sDownloadQuota <= 0);
    }
//    public static boolean notRewardOrRunOut() {
//        //      return true;
//        return ApiConstants.sDownloadQuota <= 0;
//    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayer = ((PlaybackService.LocalBinder) service).getService();
            mPlayer.registerCallback(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer.unregisterCallback(MainActivity.this);
            mPlayer = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MA = null;
        unbindService(mConnection);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(PlayEvent event) {
        if (null == mPlayList) {
            mPlayList = new PlayList();
        }
        mPlayList.getSongs().clear();
        mPlayList.songs.clear();
        mPlayList.songs.addAll(event.list);
        mPlayList.playingIndex = event.index;
        mIndex = event.index;

        if (null != mPlayer) {
            playOrPauseIv.setImageResource(R.drawable.al_icon_play_white);
            seekBarProgress.setProgress(0);
            textViewProgress.setText("00:00");
            seekTo(0);
            mHandler.removeCallbacks(mProgressCallback);
            playSong(mPlayList, mIndex);
        }
//        AdManager.getInstance().tryShowDownloadWithRate();
        MaxDownloadInterstitial.getInstance().show();
        try {
            FlurryEventReport.logListen(event.list.get(event.index).channel);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void playSong(PlayList playList, int playIndex) {
        if (playList == null) return;

        playList.setPlayMode(vPrefsUtils.lastPlayMode());
        // boolean result =
        mPlayer.play(playList, playIndex);

        Music song = playList.getCurrentSong();
        if (null != song) {
            onSongUpdated(song);
        }
    }

    private void initViewPages() {
        mFragments.clear();
        mFragments.add(new MainFragment());
        mFragments.add(new DownloadFragment());
        // 初始化Adapter这里使用FragmentPagerAdapter
        mAdpter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "";
            }
        };
        mainViewpager.setAdapter(mAdpter);
        mainViewpager.setOffscreenPageLimit(mAdpter.getCount());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mProgressCallback);
    }

    private void updateProgressTextWithProgress(int progress) {
        int targetDuration = getDuration(progress);
        textViewProgress.setText(Timeutils.formatDuration(targetDuration));
    }

    private void updateProgressTextWithDuration(long progress, long duration) {
        textViewProgress.setText(Timeutils.formatDuration(progress));
        textViewDuration.setText(Timeutils.formatDuration(duration));
    }

    private void seekTo(int duration) {
        mPlayer.seekTo(duration);
    }

    private int getDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / seekBarProgress.getMax()));
    }

    private long getCurrentSongDuration() {
        Music currentSong = mPlayer.getPlayingSong();
        if (null == currentSong) {
            return 0;
        }
        long duration = 0;
        if (currentSong != null) {
            duration = currentSong.realduration;
        }
        return duration;
    }

    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (mPlayer.isPlaying()) {
                int progress = (int) (seekBarProgress.getMax()
                        * ((float) mPlayer.getProgress() / (float) getCurrentSongDuration()));
                updateProgressTextWithDuration(mPlayer.getProgress(), mPlayer.getDuration());
                if (progress >= 0 && progress <= seekBarProgress.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBarProgress.setProgress(progress, true);
                    } else {
                        seekBarProgress.setProgress(progress);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            }
        }
    };

    @Override
    public void onSwitchLast(@Nullable Music last) {
        onSongUpdated(last);
    }

    @Override
    public void onSwitchNext(@Nullable Music next) {
        onSongUpdated(next);
    }

    @Override
    public void onComplete(@Nullable Music next) {

    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        playOrPauseIv.setImageResource(isPlaying ? R.drawable.icon_pause_white : R.drawable.al_icon_play_white);
        if (isPlaying) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        } else {
            mHandler.removeCallbacks(mProgressCallback);
        }
    }

    @Override
    public void onLoading(boolean isLoading) {
        if (isLoading) {
            loadingV.setVisibility(View.VISIBLE);
        } else {
            loadingV.setVisibility(View.GONE);
        }
    }

    public void onSongUpdated(@Nullable Music song) {
        if (song == null) {
            playOrPauseIv.setImageResource(R.drawable.al_icon_play_white);
            seekBarProgress.setProgress(0);
            updateProgressTextWithProgress(0);
            seekTo(0);
            mHandler.removeCallbacks(mProgressCallback);
            return;
        }
        titleTv.setText(song.getTitle());
        textViewDuration.setText(Timeutils.formatDuration(song.realduration));
        if (!TextUtils.isEmpty(song.getImage())) {
            ImageHelper.loadMusic(imageIv, song.getImage(), MainActivity.this, 50, 50);
        }
        mHandler.removeCallbacks(mProgressCallback);
        if (mPlayer.isPlaying()) {
            mHandler.post(mProgressCallback);
            playOrPauseIv.setImageResource(R.drawable.icon_pause_white);
        }
    }

    @Override
    public void onBackPressed() {
        if (null != mainViewpager && mainViewpager.getCurrentItem() == 1) {
            mainViewpager.setCurrentItem(0);
            return;
        }
        exitApp();
    }

    private long[] mHits = new long[2];

    //定义一个所需的数组
    private void exitApp() {
//         数组向左移位操作
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 2000)) {// 2000代表设定的间隔时间
            vPrefsUtils.sDownloadSuccessCount = 0;
            MaxOpenInterstitial.getInstance().destroy();
            MaxBackInterstitial.getInstance().destroy();
            MaxDownloadInterstitial.getInstance().destroy();
            finish();
        } else {
            ToastUtils.showShortToast("Press again to exit!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10001:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    EventBus.getDefault().postSticky(new ScanEvent(ScanEvent.SCAN_START));
                } else {
                    ToastUtils.showLongToast("For download music, please give us storage permission");
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ScanEvent event) {
        if (event.getType() == ScanEvent.SCAN_START) {
            loadMusic(new File(MyDownloadManager.getInstance().getDownloadPath()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ARewardEvent event) {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull final File folder) {
        //scan folder music
        loadMusic(folder);
    }

    private void loadMusic(final File folder) {
        new AsyncTask<Void, Void, List<Music>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (scanDialog != null) {
                    if (scanDialog.isShowing()) {
                        scanDialog.dismiss();
                    }
                    scanDialog = null;
                }
                scanDialog = new ProgressDialog(MainActivity.this);
                scanDialog.setTitle("Import mp3");
                scanDialog.setMessage("scan " + folder.getAbsolutePath());
                scanDialog.setCancelable(false);
                scanDialog.show();
            }

            @Override
            protected List<Music> doInBackground(Void... voids) {
                return Utils.scanDir(folder.getAbsolutePath());
            }

            @Override
            protected void onPostExecute(List<Music> musicList) {
                super.onPostExecute(musicList);
                if (scanDialog != null) {
                    if (scanDialog.isShowing()) {
                        scanDialog.dismiss();
                    }
                    scanDialog = null;
                }
                //与现有的music list 合并
                MyDownloadManager.getInstance().updateMuiscList(musicList);
            }
        }.executeOnExecutor(Utils.sExecutorService);
    }

}
