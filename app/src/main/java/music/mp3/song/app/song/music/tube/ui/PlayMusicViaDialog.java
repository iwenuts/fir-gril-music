package music.mp3.song.app.song.music.tube.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.databinding.DialogPlayMusicViaBinding;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;
import music.mp3.song.app.song.music.tube.player.APlayer;
import music.mp3.song.app.song.music.tube.recommend.RecommendUtils;
import music.mp3.song.app.song.music.tube.referrer.ReferrerStream;
import music.mp3.song.app.song.music.tube.ztools.ViaDialogCountUtil;

public class PlayMusicViaDialog extends Dialog implements View.OnClickListener {
    private DialogPlayMusicViaBinding binding;
    private ArrayList<Music> mArrayList;
    private int mIndex;

    public PlayMusicViaDialog(Context context, ArrayList<Music> arrayList, int index) {
        // 在构造方法里, 传入主题
        super(context, R.style.BottomDialogStyle);
        mArrayList = arrayList;
        mIndex = index;
        // 拿到Dialog的Window, 修改Window的属性
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        attributes.width = width / 5 * 4;
        attributes.gravity = Gravity.CENTER;
        window.setAttributes(attributes);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_play_music_via, null, false);
        setContentView(view);
        binding = DataBindingUtil.bind(view);
        binding.externalPlay.setOnClickListener(this);
        binding.builtInPlay.setOnClickListener(this);
        ReferrerStream referrerStream = MusicApp.config.referrer;
        String icon = referrerStream.player_feature.icon;
        String title = referrerStream.player_feature.title;
        binding.externalText.setText(title);
        Glide.with(binding.externalIcon).load(icon).into(binding.externalIcon);
        setOnShowListener(dialog -> {
            FlurryEventReport.viaDialogShow();
            ViaDialogCountUtil.reduceCount();
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.external_play) {
            ReferrerStream referrerStream = MusicApp.config.referrer;
            String pkg = referrerStream.player_feature.getPkg(true);
            RecommendUtils.gotoGP(pkg);
            FlurryEventReport.goGp("way1");
            dismiss();
        }
        if (id == R.id.built_in_play) {
            try {
                APlayer.playList(mArrayList, mIndex);
                FlurryEventReport.built_in("way1");
            } catch (Throwable e) {
                e.printStackTrace();
            }
            dismiss();
        }
    }


}
