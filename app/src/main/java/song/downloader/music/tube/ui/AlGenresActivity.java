package song.downloader.music.tube.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import song.downloader.music.tube.R;
import song.downloader.music.tube.bean.GenreBean;
import song.downloader.music.tube.ztools.ImageHelper;
import song.downloader.music.tube.ztools.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlGenresActivity extends BaseActivity {

    private static final String KEY_GENRES = "GENRES";

    public static void start(Context context, ArrayList<GenreBean> genres) {
        Intent starter = new Intent(context, AlGenresActivity.class);
        starter.putParcelableArrayListExtra(KEY_GENRES, genres);
        if (!(context instanceof Activity)) {
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(starter);
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv)
    RecyclerView rv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_daily_picks_and_genres;
    }

    private int dp8;

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        mToolbar.setTitle("Genres");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv.setLayoutManager(new GridLayoutManager(this, 3));
        int dp7 = Utils.dip2px(this, 7);
        dp8 = Utils.dip2px(this, 8);
        rv.setPadding(dp7, 0, dp7, 0);
        rv.addItemDecoration(new ItemDecor());
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

    @Override
    protected void initDatas() {
        ArrayList<GenreBean> genres = getIntent().getParcelableArrayListExtra(KEY_GENRES);
        GenresAdapter adapter = new GenresAdapter(genres);
        rv.setAdapter(adapter);
    }

    class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.Holder> {

        ArrayList<GenreBean> genres;

        GenresAdapter(ArrayList<GenreBean> genres) {
            this.genres = genres;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup p, int i) {
            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.item_genre_layout, p, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int pos) {
            GenreBean bean = genres.get(pos);
            ImageHelper.loadMusic(holder.iv, bean.image, AlGenresActivity.this, 0, 0);
            holder.tv.setText(bean.title.trim());
        }

        @Override
        public int getItemCount() {
            return genres == null ? 0 : genres.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            private final ImageView iv;
            private final TextView tv;

            Holder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        if (pos >= 0) {
                            onItemClick(genres.get(pos));
                        }
                    }
                });

                iv = itemView.findViewById(R.id.genre_iv);
                tv = itemView.findViewById(R.id.genre_title_tv);
            }
        }
    }

    private void onItemClick(GenreBean bean) {
        DailyPickGenreListActivity.start(this, bean);
    }

    private class ItemDecor extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = dp8;
            outRect.right = dp8;
        }
    }
}
