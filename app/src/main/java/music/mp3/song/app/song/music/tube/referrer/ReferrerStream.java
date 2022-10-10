package music.mp3.song.app.song.music.tube.referrer;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import music.mp3.song.app.song.music.tube.bean.BaseBean;

public class ReferrerStream extends BaseBean {

    public List<AGeneralReferrer> general;

    public SpecialReferrer open_dir;
    public SpecialReferrer player_banner; // also use as item_list
    public SpecialReferrer player_feature; // include equalizer and sleep timer
    public SpecialReferrer player_cast;
    public SpecialReferrer player_tube;

    public AGeneralReferrer getGeneralById(String id) {
        if (general == null) {
            return null;
        }

        for (AGeneralReferrer referrer : general) {
            if (referrer.isInvalid()) {
                continue;
            }
            if (TextUtils.equals(id, referrer.id)) {
                return referrer;
            }
        }
        return null;
    }

    public AGeneralReferrer getGeneral(String type) {
        if (general == null) {
            return null;
        }
        for (AGeneralReferrer referrer : general) {
            if (referrer.isInvalid()) {
                continue;
            }
            if (TextUtils.equals(type, referrer.type)) {
                return referrer;
            }
        }
        return null;
    }

    public AGeneralReferrer getGeneral(String type, String id) {
        if (general == null) {
            return null;
        }
        for (AGeneralReferrer referrer : general) {
            if (referrer.isInvalid()) {
                continue;
            }
            if (TextUtils.equals(type, referrer.type) && TextUtils.equals(id, referrer.id)) {
                return referrer;
            }
        }
        return null;
    }

    public List<String> getAllImage() {
        List<String> images = new ArrayList<>();
        if (general != null) {
            for (AGeneralReferrer referrer : general) {
                if (referrer.isType(AGeneralReferrer.TYPE_SLIDE_MENU) || referrer.isType(AGeneralReferrer.TYPE_TITLEBAR_MENU)) {
                    for (ReferrerItem item : referrer.items) {
                        if (item.isInvalid()) {
                            continue;
                        }
                        addImage(images, item);
                    }
                }
            }
        }
        if (open_dir != null && !open_dir.isInvalid()) {
            addImage(images, open_dir);
        }
        if (player_banner != null && !player_banner.isInvalid()) {
            addImage(images, player_banner);
        }
        if (player_feature != null && !player_feature.isInvalid()) {
            addImage(images, player_feature);
        }
        if (player_cast != null && !player_cast.isInvalid()) {
            addImage(images, player_cast);
        }
        if (player_tube != null && !player_tube.isInvalid()) {
            addImage(images, player_tube);
        }
        return images;
    }

    private void addImage(List<String> images, BaseReferrer referrer) {
        images.add(referrer.icon);
        images.add(referrer.poster);
    }
}
