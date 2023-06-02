package music.mp3.song.app.song.music.tube.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;
import music.mp3.song.app.song.music.tube.network.JamApi;
import music.mp3.song.app.song.music.tube.bean.GenreBean;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.admax.MaxMrec;
import music.mp3.song.app.song.music.tube.admax.MaxSearchBanner;
import music.mp3.song.app.song.music.tube.player.APlayer;
import music.mp3.song.app.song.music.tube.referrer.AGeneralReferrer;
import music.mp3.song.app.song.music.tube.referrer.ReferrerItem;
import music.mp3.song.app.song.music.tube.referrer.ReferrerStream;
import music.mp3.song.app.song.music.tube.widget.ReferrerBannerLayout;
import music.mp3.song.app.song.music.tube.ztools.ImageHelper;
import music.mp3.song.app.song.music.tube.ztools.ShareUtils;
import music.mp3.song.app.song.music.tube.ztools.Utils;
import music.mp3.song.app.song.music.tube.recommend.RecommendBean;
import music.mp3.song.app.song.music.tube.recommend.RecommendManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import co.tagview.TagLayout;
import co.tagview.TagView;

/**
 *
 */
public class MainFragment extends BaseFragment {

    private Unbinder unbinder;
    ArrayList<String> tags = new ArrayList<>(Arrays.asList(MusicApp.config.tags.split("\\|")));

    @BindView(R.id.banner_ll)
    LinearLayout bannerLl;
    @BindView(R.id.tagView)
    TagLayout tagContainerLayout;
    @BindView(R.id.layout_notice)
    LinearLayout layout_notice;
    @BindView(R.id.banner_layout)
    ReferrerBannerLayout mReferrerBannerLayout;



    @BindView(R.id.daily_picks_panel)
    LinearLayout dailyPickLL;
    @BindView(R.id.genres_panel)
    View genresPanel;
    @BindView(R.id.genres_ll_panel)
    LinearLayout genresLL;

    @BindView(R.id.mrecLayout)
    LinearLayout mrecLayout;


    private JamApi jamApi = new JamApi();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, parentView);
//        String string = App.config.notice;
//        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(App.config.noticeId)) {
//            this.layout_notice.setVisibility(View.GONE);
//        } else {
//            this.tvNotice.setText(string);
//        }
    }

    @Override
    protected void initListener() {
//        tvLink.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                if (!TextUtils.isEmpty(App.config.noticeId)) {
//                    ShareUtils.gotoRecommend(getActivity(), App.config.noticeId);
//                }
//            }
//        });
        tagContainerLayout.setTags(tags);
        tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {

            @Override
            public void onTagClick(int position, String text) {
                SearchActivity.launch(getContext(), text);

            }

            @Override
            public void onTagLongClick(final int position, String text) {
                SearchActivity.launch(getContext(), text);
            }

            @Override
            public void onTagCrossClick(int position) {
                if (position >= 0 && position < tags.size()) {
                    SearchActivity.launch(getContext(), tags.get(position));

                }
            }
        });
    }

    @Override
    public void initDatas() {
        loadBanner();
        loadMrec();

        loadDailyPicks();
        loadGenres();
    }

    private Type type = new TypeToken<ArrayList<GenreBean>>() {
    }.getType();

    private ArrayList<GenreBean> genres;

    private void loadGenres() {
        if (!MusicApp.config.showGenre) {
            return;
        }

        if (genres != null && !genres.isEmpty()) {
            dealGenres(genres);
            return;
        }

        String json = Utils.readAsset("genres.json");
        ArrayList<GenreBean> genres = Utils.fromJson(json, type);
        dealGenres(genres);
    }

    private void dealGenres(ArrayList<GenreBean> list) {
        if (list == null || list.isEmpty()) {
            genresPanel.setVisibility(View.GONE);
        } else {
            genres = list;
            int size = list.size();
            LayoutInflater inflater = getLayoutInflater();
            for (int i = 0; i < 3 && i < size; ++i) {
                View itemView = inflater.inflate(R.layout.item_genre_layout, genresLL, false);

                itemView.setOnClickListener(genreItemClick);
                itemView.setTag(i);

                ImageView iv = itemView.findViewById(R.id.genre_iv);
                TextView tv = itemView.findViewById(R.id.genre_title_tv);

                GenreBean bean = list.get(i);

                ImageHelper.loadMusic(iv, bean.image, getContext(), 0, 0);
                tv.setText(bean.title.trim());
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) itemView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
                if (i > 0) {
                    lp.leftMargin = Utils.dip2px(getContext(), 20);
                }
                genresLL.addView(itemView);
            }
        }
    }

    private View.OnClickListener genreItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GenreBean bean = genres.get((Integer) v.getTag());
            DailyPickGenreListActivity.start(getActivity(), bean);
        }
    };

    private void loadDailyPicks() {
        if (!MusicApp.config.showDaily) {
            return;
        }
        if (dailyPicks != null && !dailyPicks.isEmpty()) {
            dealDailyPicks(dailyPicks);
            return;
        }
        jamApi.popular(DailyPickGenreListActivity.DAILY_PICKS_GENRES_PAGE_SIZE, 0, new JamApi.JamCallback() {
            @Override
            public void onLoadSuc(List<Music> list) {
                dealDailyPicks(list);
            }

            @Override
            public void onError(boolean empty) {
            }
        });
    }

    private ArrayList<Music> dailyPicks;

    private void dealDailyPicks(List<Music> list) {
        if (list == null || list.isEmpty() || unsafe()) {
            return;
        }
        dailyPicks = new ArrayList<>(list);
        int size = list.size();
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < 3 && i < size; i++) {
            View itemView = inflater.inflate(R.layout.item_daily_pick_layout,
                    dailyPickLL, false);
            itemView.setTag(i);
            itemView.setOnClickListener(dailyPickClick);

            ImageView iv = itemView.findViewById(R.id.image_iv);
            TextView titleTv = itemView.findViewById(R.id.title_tv);
            TextView artistTv = itemView.findViewById(R.id.artist_tv);

            Music music = list.get(i);
            ImageHelper.loadMusic(iv, music.getImage(), getContext(), 60, 60);
            titleTv.setText(music.getTitle());
            artistTv.setText(music.getArtistName());

            dailyPickLL.addView(itemView);
        }
        dailyPickLL.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener dailyPickClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            APlayer.playList(dailyPicks, (Integer) v.getTag());
        }
    };

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        jamApi.destroy();
        super.onDestroyView();
        MaxSearchBanner.getInstance().stopAutoRefresh();
        MaxSearchBanner.getInstance().destroy();
        MaxMrec.getInstance().stopAutoRefresh();
        MaxMrec.getInstance().destroy();
    }
    @Override
    public void onStart() {
        super.onStart();
        ReferrerStream referrer = MusicApp.config.getUseReferrer();
        if (referrer == null || referrer.getGeneral(AGeneralReferrer.TYPE_TITLEBAR_MENU) == null
                || referrer.getGeneral(AGeneralReferrer.TYPE_TITLEBAR_MENU).isInvalid()) {
            this.layout_notice.setVisibility(View.GONE);
            FlurryEventReport.referrer("show", "false");
        } else {
            this.layout_notice.setVisibility(View.VISIBLE);
            FlurryEventReport.referrer("show", "true");
            AGeneralReferrer general = referrer.getGeneral(AGeneralReferrer.TYPE_TITLEBAR_MENU);
            List<ReferrerItem> items = general.getValidItems();
            mReferrerBannerLayout.loadReferrer(this.getContext(), items);
        }
    }

    @OnClick(R.id.daily_picks_tv)
    void onDailyPickClick() {
        DailyPickGenreListActivity.start(getActivity(), dailyPicks);
    }

    @OnClick(R.id.genres_tv)
    void onGenresClick() {
        AlGenresActivity.start(getActivity(), genres);
    }

    private void loadMrec() {
        if (!MusicApp.config.showNative) {
            return;
        }
        if (mrecLayout == null) {
            return;
        }
        if (getActivity() == null) {
            return;
        }

        MaxMrec.getInstance().createMrecAd(getActivity(), mrecLayout);

    }


    private void loadBanner() {
//        if (!App.config.showBanner2 || unsafe()) {
//            return;
//        }

        if (getActivity() == null) {
            return;
        }

        if (MusicApp.config.ad && bannerLl != null) {
            MaxSearchBanner.getInstance().createBannerAd(getActivity(), bannerLl);
        }
//        AdManager.getInstance().loadBanner2(getActivity(), new BannerListener() {
//            @Override
//            public void onAdLoaded(BaseBanner banner) {
//                if (null != bannerLl) {
//                    bannerLl.setVisibility(View.VISIBLE);
//                    banner.show(bannerLl);
//                }
//            }
//        });
    }

}
