package song.downloader.music.tube.ui;

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

import song.downloader.music.tube.MusicApp;
import song.downloader.music.tube.R;
import song.downloader.music.tube.bean.Music;
import song.downloader.music.tube.databinding.DialogPlayMusicViaBinding;
import song.downloader.music.tube.firebase.FlurryEventReport;
import song.downloader.music.tube.player.APlayer;
import song.downloader.music.tube.recommend.RecommendUtils;
import song.downloader.music.tube.referrer.ReferrerStream;
import song.downloader.music.tube.ztools.ViaDialogCountUtil;

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
