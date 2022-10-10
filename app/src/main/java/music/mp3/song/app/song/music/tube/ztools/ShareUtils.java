package music.mp3.song.app.song.music.tube.ztools;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;


public class ShareUtils {
//    public static void sendText(Context act, String text) {
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
//        shareIntent.setType("text/plain");
//        // 设置分享列表的标题，并且每次都显示分享列表
//        act.startActivity(Intent.createChooser(shareIntent, "Share To"));
//    }

    public static void gotoRecommend(Context context, String names) {
        //用 || 分隔 names, 对每个name：判断是否已安装，若安装则跳下一个，全部已安装则跳返回""
        String[] apps = names.split("\\|\\|");
        String name = checkRecommendExist(context, apps);

        if (name.startsWith("pub:")) {
            String pubName = name.substring(4);
            gotoMoreApps(context, pubName);
        } else {
            gotoGoogePlayStore(context, name);
        }
    }

    public static void gotoGoogePlayStore(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return;
        String queryString = packageName;
        try {
            Intent launchIntent = new Intent(Intent.ACTION_VIEW);
            launchIntent.setPackage("com.android.vending");
            launchIntent.setData(Uri.parse("market://details?id=" + queryString));
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        } catch (ActivityNotFoundException ee) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + queryString)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param context
     * @param pubName
     */
    public static void gotoMoreApps(Context context, String pubName) {
        try {
            if (TextUtils.isEmpty(pubName))
                return;
            String queryString = pubName;
            try {
                //@see https://stackoverflow.com/questions/11753000/how-to-open-the-google-play-store-directly-from-my-android-application
                //关键在于同时设置： Intent.ACTION_VIEW 和 "com.android.vending"
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.android.vending");
                // make sure it does NOT open in the stack of your activity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setData(Uri.parse("market://search?q=pub:" + queryString));
                context.startActivity(intent);
            } catch (ActivityNotFoundException ee) {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + queryString)));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static  void openBrowser(Context context,String url){
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
//            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            // 打印Log   ComponentName到底是什么
            context.startActivity(Intent.createChooser(intent, "Open"));
        } else {
        }
    }

    private static String checkRecommendExist(Context context, String[] list) {
        List<PackageInfo> packages = null;
        for (String recommendName : list) {
            if (recommendName.startsWith("pub:")) {
                return recommendName;
            }
            if (packages == null) {
                packages = context.getPackageManager().getInstalledPackages(0);
            }
            boolean found = false;
            String nameId = recommendName.split("&")[0];
            for (PackageInfo packageInfo : packages) {
                if (packageInfo.packageName.equals(nameId)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return recommendName;
            }
        }
        return "";
    }
}
