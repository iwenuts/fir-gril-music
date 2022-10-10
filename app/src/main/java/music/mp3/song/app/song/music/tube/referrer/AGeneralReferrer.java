package music.mp3.song.app.song.music.tube.referrer;

import android.text.TextUtils;
 

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import music.mp3.song.app.song.music.tube.bean.BaseBean;
import music.mp3.song.app.song.music.tube.ztools.AppUtil;

public class AGeneralReferrer extends BaseBean {

    public static final String TYPE_SLIDE_MENU = "slide";
    public static final String TYPE_TITLEBAR_MENU = "titlebar";
    public static final String TYPE_BANNER = "banner";
    public static final String TYPE_INTERSTITIAL = "interstitial";
    public static final String TYPE_NATIVE = "native";
    public static final String TYPE_REWARD = "reward";

    public List<ReferrerItem> items;

    public String type;
    public String id;

    public int percent = 20; // what percent will show first
    public int loop_interval = 15; // in seconds, only used for banner and native
    public int click_area = Integer.MAX_VALUE; // only used for banner, native, default the whole item can click
    public int timer = 0; // only used for interstitial and reward, close timer

    public boolean isType(String type) {
        return TextUtils.equals(this.type, type);
    }

    public boolean isInvalid() {
        if (items == null || items.isEmpty()) {
            return false;
        }
        return getValidItems().isEmpty();
    }

    public List<ReferrerItem> getValidItems() {
        List<ReferrerItem> list = new ArrayList<>();
        if (items != null) {
            for (ReferrerItem item : items) {
                if (!item.isInvalid()) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    public boolean isInvalidNotInstall() {
        if (items == null || items.isEmpty()) {
            return false;
        }
        return getValidNotInstallItems().isEmpty();
    }

    public List<ReferrerItem> getValidNotInstallItems() {
        List<ReferrerItem> items = getValidItems();
        Iterator<ReferrerItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            ReferrerItem next = iterator.next();
            if (AppUtil.appInstalled(next.getPkg(false))) {
                iterator.remove();
            }
        }
        return items;
    }

}
