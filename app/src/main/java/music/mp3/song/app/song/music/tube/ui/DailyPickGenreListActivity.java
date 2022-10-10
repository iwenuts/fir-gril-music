package music.mp3.song.app.song.music.tube.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.adapter.AMusicAdapter;
import music.mp3.song.app.song.music.tube.network.JamApi;
import music.mp3.song.app.song.music.tube.bean.GenreBean;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.firebase.Referrer;
import music.mp3.song.app.song.music.tube.admax.MaxBackInterstitial;
import music.mp3.song.app.song.music.tube.ztools.ToastUtils;
import music.mp3.song.app.song.music.tube.recommend.RecommendManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyPickGenreListActivity extends BaseActivity implements AMusicAdapter.BindCallback {

    private static final String KEY_DATAS = "DATAS";
    private static final String KEY_WORK_MODE = "WORK_MODE";
    private static final String KEY_GENRE = "Tag_ID";

    public static final int DAILY_PICKS_GENRES_PAGE_SIZE = 20;
    private static final int WORK_MODE_DAILY_PICK = 0;
    private static final int WORK_MODE_GENRE = 1;

    public static void start(Context context, ArrayList<Music> list) {
        Intent starter = new Intent(context, DailyPickGenreListActivity.class);
        starter.putParcelableArrayListExtra(KEY_DATAS, list);
        starter.putExtra(KEY_WORK_MODE, WORK_MODE_DAILY_PICK);
        if (!(context instanceof Activity)) {
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(starter);
    }

    public static void start(Context context, Parcelable genre) {
        Intent starter = new Intent(context, DailyPickGenreListActivity.class);
        starter.putExtra(KEY_WORK_MODE, WORK_MODE_GENRE);
        starter.putExtra(KEY_GENRE, genre);
        if (!(context instanceof Activity)) {
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(starter);
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv)
    RecyclerView rv;

    @BindView(R.id.progress_bar)
    View progress;

    private JamApi jamApi = new JamApi();
    private List<Music> musics = new ArrayList<>();
    private AMusicAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_daily_picks_and_genres;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);

        workMode = getIntent().getIntExtra(KEY_WORK_MODE, WORK_MODE_DAILY_PICK);
        if (workMode == WORK_MODE_DAILY_PICK) {
            mToolbar.setTitle("Daily Picks");
        } else {
            progress.setVisibility(View.VISIBLE);
            genre = getIntent().getParcelableExtra(KEY_GENRE);
            mToolbar.setTitle(genre.title.trim());
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new AMusicAdapter(this, musics);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        adapter.setBindCallback(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int workMode = WORK_MODE_DAILY_PICK;
    private GenreBean genre;

    @Override
    protected void initDatas() {
        if (workMode == WORK_MODE_DAILY_PICK) {
            ArrayList<Music> list = getIntent().getParcelableArrayListExtra(KEY_DATAS);
            musics.addAll(list);
        } else {
            loadGenre();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jamApi.destroy();
        if (MaxBackInterstitial.getInstance().isReady()) {
            MaxBackInterstitial.getInstance().show();
        } else if (!Referrer.isBanUser()) {
            RecommendManager.getInstance().showRecommend(MusicApp.sContext);
        }
    }

    private boolean loading = false;
    private boolean noMore = false;

    @Override
    public void onBindAt(int position) {
        if (position + 5 >= musics.size()) {
            loadNext();
        }
    }

    private void loadNext() {
        if (loading || noMore) {
            return;
        }
        ToastUtils.showShortToast("Loading Next Page");
        loading = true;
        if (workMode == WORK_MODE_GENRE) {
            loadGenre();
        } else {
            loadDailyPick();
        }
    }

    private JamApi.JamCallback netCallback = new JamApi.JamCallback() {
        @Override
        public void onLoadSuc(List<Music> list) {
            loading = false;
            progress.setVisibility(View.GONE);
            if (isFinishing()) {
                return;
            }
//            ToastUtils.showShortToast("Load Completed");
            if (list != null && !list.isEmpty()) {
                musics.addAll(list);
                adapter.notifyDataSetChanged();
            } else {
                noMore = true;
            }
        }

        @Override
        public void onError(boolean empty) {
            loading = false;
            progress.setVisibility(View.GONE);
            ToastUtils.showShortToast("Error");
        }
    };

    private void loadGenre() {
        jamApi.tracksByTag(genre.id, DAILY_PICKS_GENRES_PAGE_SIZE, musics.size(), netCallback);
    }

    private void loadDailyPick() {
        jamApi.popular(DAILY_PICKS_GENRES_PAGE_SIZE, musics.size(), netCallback);
    }
}
