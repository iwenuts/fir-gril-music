package music.mp3.song.app.song.music.tube.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.util.List;

import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;
import music.mp3.song.app.song.music.tube.referrer.ReferrerItem;
import music.mp3.song.app.song.music.tube.ztools.ShareUtils;

public class ReferrerBannerLayout extends FrameLayout {


   public ReferrerBannerLayout(@NonNull Context context) {
      this(context, null);
   }

   public ReferrerBannerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public ReferrerBannerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

   }

   public void loadReferrer(Context context, List<ReferrerItem> items) {
      View rootView = LayoutInflater.from(context).inflate(R.layout.referrer_banner_layout, this, true);
      MZBannerView<ReferrerItem> bannerView = rootView.findViewById(R.id.banner_view);
      bannerView.setVisibility(VISIBLE);
      bannerView.setBannerPageClickListener((view, position) -> {
         if (items != null && !items.isEmpty()) {
            ReferrerItem item = items.get(position);
            if (!TextUtils.isEmpty(item.webappurl)) {
               FlurryEventReport.referrer("click_web", item.webappurl);
               ShareUtils.openBrowser(context, item.webappurl);
            } else {
               FlurryEventReport.referrer("click_pkg", item.getPkg(true));
               ShareUtils.gotoGoogePlayStore(context, item.getPkg(true));
            }
         }
      });
      bannerView.setIndicatorVisible(false);
      bannerView.setPages(items, () -> new BannerReferrerItemHolder());
      bannerView.start();

   }


   public class BannerReferrerItemHolder implements MZViewHolder<ReferrerItem> {
      private ImageView adLogoImage;
      private TextView adTitleText;
      private TextView descriptionText;

      @Override
      public View createView(Context context) {
         View view = LayoutInflater.from(context).inflate(R.layout.banner_item_holder, null, false);
         adLogoImage = (ImageView) view.findViewById(R.id.ad_logo);
         adTitleText = view.findViewById(R.id.ad_title);
         descriptionText = view.findViewById(R.id.ad_description);
         return view;
      }

      @Override
      public void onBind(Context context, int position, ReferrerItem item) {
         String title = item.title;
         String subtitle = item.subtitle;
         String logourl = item.logourl;
         adTitleText.setText(title);
         descriptionText.setText(subtitle);
         Glide.with(adLogoImage).load(logourl).into(adLogoImage);

      }

   }

}
