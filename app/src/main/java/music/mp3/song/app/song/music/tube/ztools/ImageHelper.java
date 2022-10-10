package music.mp3.song.app.song.music.tube.ztools;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import music.mp3.song.app.song.music.tube.R;


public class ImageHelper {
    public static void loadMusic(ImageView iv, String url, Context context, int width, int height) {
        try {
            if (width <= 0 || height <= 0) {
                Glide.with(iv.getContext()).load(url).apply(new RequestOptions()
                        .placeholder(R.drawable.ic_default_cover)
                        .error(R.drawable.ic_default_cover)
                ).into(iv);
            } else {
                Glide.with(iv.getContext()).load(url).apply(new RequestOptions()
                        .placeholder(R.drawable.ic_default_cover)
                        .error(R.drawable.ic_default_cover)
                        .override(Utils.dip2px(context, width), Utils.dip2px(context, height))
                ).into(iv);
            }
        } catch (Throwable e) {
            iv.setImageResource(R.drawable.ic_default_cover);
        }

    }
}
