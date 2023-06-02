package music.mp3.song.app.song.music.tube.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.BuildConfig;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.network.ApiConstants;
import music.mp3.song.app.song.music.tube.bean.ScanEvent;
import music.mp3.song.app.song.music.tube.firebase.Referrer;
import music.mp3.song.app.song.music.tube.admax.MaxBackInterstitial;
import music.mp3.song.app.song.music.tube.ztools.ToastUtils;

import music.mp3.song.app.song.music.tube.recommend.RecommendManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity {
    private static final String Q = "q";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;


    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private static final List<String> mAllTitles = new ArrayList<String>() {{
        add("Server1");
        add("Server2");
        add("Server3");
        add("Server4");
        add("Server5");
        add("Server6");
        add("Server7");
        add("Server8");
        add("Server9");
        add("Server10");
        add("Server11");
        add("Server12");
        add("Server13");
    }};
    private FragmentPagerAdapter mAdpter;

    private String mQuery;


    public static void launch(Context context, String query) {
        Intent launcher = new Intent(context, SearchActivity.class);
        launcher.putExtra(Q, query);

        if (context instanceof Activity) {
        } else {
            launcher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(launcher);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initBundleExtra(Bundle savedInstanceState) {
        super.initBundleExtra(savedInstanceState);
        mQuery = getIntent().getStringExtra(Q);
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
    }

    @Override
    protected void initDatas() {
        mToolbar.setTitle("Search: " + mQuery);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViewPages();
        initTab();
//        if (BuildConfig.DEBUG) {
//            MyApp.appLovinSdk.showMediationDebugger();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initTab() {
        if (null != mFragments && mFragments.size() <= 4) {
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.dark_gray), ContextCompat.getColor(this, R.color.white));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
        ViewCompat.setElevation(mTabLayout, 10);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    private void addSuper() {
        if (BuildConfig.DEBUG) {
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_YT, mQuery));
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_JAMENDO, mQuery));
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_WYY, mQuery));
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_NHAC, mQuery));
            return;
        }
        if (MusicApp.config.yt) {
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_YT, mQuery));
        }

        if (MusicApp.config.wyy) {
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_WYY, mQuery));
        }
        if (MusicApp.config.nhac) {
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_NHAC, mQuery));
        }

        mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_JAMENDO, mQuery));
    }

    private void addNormal() {
        if (MusicApp.config.jm) {
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_JAMENDO, mQuery));
        } else if (MusicApp.config.xm) {
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_XM, mQuery));
        } else if (MusicApp.config.sc) {
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_SOUND, mQuery));
        } else if (MusicApp.config.yt) {
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_YT, mQuery));
        }
    }

    private void initViewPages() {
        if (Referrer.isBanUser()) {
            addNormal();
        } else if (Referrer.isSuper()) {
            addSuper();
        } else if (ApiConstants.onlist) {
            addNormal();
        } else if (Referrer.isCnUser()) {
            addSuper();
        } else {
            addNormal();
        }

        if (MusicApp.openAbsoluteShow == 10) {
            mFragments.clear();
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_YT, mQuery));
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_JAMENDO, mQuery));
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_WYY, mQuery));
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_NHAC, mQuery));
        }

        if (BuildConfig.DEBUG) {
            mFragments.clear();
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_YT, mQuery));
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_JAMENDO, mQuery));
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_WYY, mQuery));
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_NHAC, mQuery));
        }


        //Jamendo保底
        if (mFragments.size() < 1) {
            mFragments.add(ResultFragment.newInstance(ResultFragment.TYPE_JAMENDO, mQuery));
        }

        mTitles = mAllTitles.subList(0, mFragments.size());
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
                return mTitles.get(position);
            }
        };
        mViewPager.setOffscreenPageLimit(mFragments.size());
        mViewPager.setAdapter(mAdpter);
        if (mFragments.size() < 2) {
            mTabLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MaxBackInterstitial.getInstance().isReady()) {
            MaxBackInterstitial.getInstance().showDialog(MainActivity.MA);
            MaxBackInterstitial.getInstance().show();
        } else if (MusicApp.normalUser) {
            RecommendManager.getInstance().showRecommend(MusicApp.sContext);
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
}
