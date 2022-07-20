package song.downloader.music.tube.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import song.downloader.music.tube.R;
import song.downloader.music.tube.ui.MyDownloadManager;
import song.downloader.music.tube.ui.RingtoneDialog;
import song.downloader.music.tube.bean.Music;
import song.downloader.music.tube.ztools.ImageHelper;
import song.downloader.music.tube.ztools.vPrefsUtils;
import song.downloader.music.tube.ztools.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private PlayListener mListener;
//    private ExplosionField mExplosionField;//爆炸效果

    public DownAdapter(Context context, PlayListener listener) {
        mContext = context;
        mListener = listener;
//        mExplosionField = ExplosionField.attach2Window((Activity) mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Music track = (Music) MyDownloadManager.getInstance().getReverseAllTasks().get(position);
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.itemName.setText(track.getTitle());
        viewHolder.itemArtist.setText(track.getArtistName());
        if (!TextUtils.isEmpty(track.getImage())) {
            ImageHelper.loadMusic(viewHolder.itemIcon, track.getImage(), mContext, 60, 60);
        } else {
            viewHolder.itemIcon.setImageResource(R.drawable.ic_default_cover);
        }
        viewHolder.itemRintone.setTag(track);
        viewHolder.itemRintone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music bean = (Music) v.getTag();
                if (bean.downloadStats != Music.DL_DONE) {
                    ToastUtils.showLongToast("Must Download Finished!");
                    return;
                }
                RingtoneDialog dialog = new RingtoneDialog(v.getContext());
                dialog.setBean(bean);
                dialog.show();
            }
        });
        viewHolder.itemPlay.setTag(position);
        viewHolder.itemPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Music> arrayList = MyDownloadManager.getInstance().getReverseAllTasks();
                int index = (int) v.getTag();
                playClickItem(arrayList, index);

            }
        });
        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Music> arrayList = MyDownloadManager.getInstance().getReverseAllTasks();
                int index = (int) v.getTag();
                playClickItem(arrayList, index);
            }
        });


        viewHolder.itemDelete.setTag(track);
        viewHolder.itemDelete.setTag(R.string.app_name, viewHolder.itemView);
        viewHolder.itemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Music track = (Music) view.getTag();
                AlertDialog.Builder confirmDlg = new AlertDialog.Builder(mContext);
                confirmDlg.setTitle("confirm");
                confirmDlg.setMessage(track.title);
                confirmDlg.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                mExplosionField.explode((View) view.getTag(R.string.app_name));
                                MyDownloadManager.getInstance().removeTask(track);
                                notifyDataSetChanged();
                                vPrefsUtils.setDownloadCache(MyDownloadManager.getInstance().getAllTasks());
                            }
                        }, 100);
                    }
                });
                confirmDlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                confirmDlg.show();
            }
        });
        if (track.downloadStats == Music.DL_DONE) {
            viewHolder.itemProgress.setText("success");
        } else if (track.downloadStats == Music.DL_ERROR) {
            viewHolder.itemProgress.setText("error");
        } else {
            viewHolder.itemProgress.setText(track.progress > 0.0f ? track.progress + "%" : "0%");
        }
    }

    private void playClickItem(ArrayList<Music> arrayList, int index) {
        mListener.onPlay(arrayList,index);

    }


    @Override
    public int getItemCount() {
        return null == MyDownloadManager.getInstance().getReverseAllTasks() ? 0 : MyDownloadManager.getInstance().getReverseAllTasks().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_icon)
        ImageView itemIcon;
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.artist_tv)
        TextView itemArtist;
        @BindView(R.id.item_progress)
        TextView itemProgress;
        @BindView(R.id.item_play)
        ImageView itemPlay;
        @BindView(R.id.item_ringtone)
        ImageView itemRintone;
        @BindView(R.id.item_delete)
        ImageView itemDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface PlayListener {
        void onPlay(ArrayList<Music> arrayList, int index);
    }

}
