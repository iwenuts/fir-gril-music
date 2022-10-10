package music.mp3.song.app.song.music.tube.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.network.ApiConstants;
import music.mp3.song.app.song.music.tube.bean.ZlDownloadEvent;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.bean.ScanEvent;
import music.mp3.song.app.song.music.tube.firebase.FlurryEventReport;
import music.mp3.song.app.song.music.tube.ztools.FileUtil;
import music.mp3.song.app.song.music.tube.ztools.PermissionUtils;
import music.mp3.song.app.song.music.tube.ztools.vPrefsUtils;
import music.mp3.song.app.song.music.tube.ztools.ToastUtils;
import music.mp3.song.app.song.music.tube.ztools.Utils;

import download.manager.thread.DefaultRetryPolicy;
import download.manager.thread.DownloadRequest;
import download.manager.thread.DownloadStatusListenerV1;
import download.manager.thread.ThinDownloadManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MyDownloadManager {
    private static volatile MyDownloadManager instance;
    public static String folder = "MusicDownloadFree";

    private ThinDownloadManager downloadManager;

    public void cancel(int taskId) {
        mDownloads.remove(taskId);
        downloadManager.cancel(taskId);
        //
    }

    public interface DownloadListener {
        void download(Music bean);
    }

//    private String mChannelId = "1002";
//    private String mChannelName = "Download Video";
//    NotificationManager mNotificationManager;

    private volatile Map<Integer, Music> mDownloads = new HashMap<>();
    private List<Music> mNewList = new ArrayList<>();
    private List<Music> mAllList = new ArrayList<>();
    private DownloadListener downloadListener;

    public static MyDownloadManager getInstance() {
        if (instance == null) {
            synchronized (MyDownloadManager.class) {
                if (instance == null) {
                    instance = new MyDownloadManager();
                }
            }
        }
        return instance;
    }

    private MyDownloadManager() {
        downloadListener = new DownloadListenerImpl();
        downloadManager = new ThinDownloadManager();
//        mNotificationManager = (NotificationManager) App.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        if (null != vPrefsUtils.getDownloadCache()) {
            mAllList.addAll(vPrefsUtils.getDownloadCache());
        }
    }

    public void download(Music bean, Activity activity) {
        if (null == bean || TextUtils.isEmpty(bean.downloadUrl)) {
            ToastUtils.showShortToast("Error, not found download url");
            return;
        }
        //check permission
        if (!PermissionUtils.checkStoragePermissions(activity, 10001)) {
            return;
        }
        //判断是否已成功下载
        int pos = mAllList.indexOf(bean);
        if (pos >= 0) {
            if (mAllList.get(pos).downloadStats == Music.DL_DONE) {
                ToastUtils.showShortToast("This song has been downloaded");
                return;
            } else {
                //从列表中删除
                mAllList.remove(pos);
                for (Iterator<Map.Entry<Integer, Music>> it = mDownloads.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<Integer, Music> entry = it.next();
                    int taskId = entry.getKey();
                    Music downloaded = entry.getValue();
                    if (downloaded.equals(bean)) {
                        //取消本次session的下载任务
                        downloadManager.cancel(taskId);
                        it.remove();
                        break;
                    }
                }
            }
        }
        String savePath = getDownloadPath() + File.separator + bean.getValidFileName();

        Uri destinationUri = Uri.parse(savePath);
        if (destinationUri == null) {
            ToastUtils.showShortToast("Sdcard not mounted……");
            return;
        }
        //与 thinDownloader 中的文件路径获取方式保持一致
        savePath = destinationUri.getPath();

        //删除以前的文件
        try {
            if (new File(savePath).exists()) {
                new File(savePath).delete();
            }
        } catch (Throwable e) {

        }
        if (bean.channel != Music.CHANNEL_MP3juice) {
            downloadListener.download(bean);
        }
        ToastUtils.showShortToast("Downloading……");
        Uri downloadUri = Uri.parse(bean.downloadUrl);

        DownloadRequest downloadRequest;

//        if (bean.channel == Music.CHANNEL_SOUND) {
//            downloadRequest = new HlsDownloadRequest(downloadUri)
//                    .setDestinationURI(destinationUri)
//                    .setPriority(DownloadRequest.Priority.HIGH)
//                    .setStatusListener(sListener);
//        } else {
        downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri)
                .setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadContext(MusicApp.getInstance())//Optional
                .addCustomHeader("Connection", "close")
                .setStatusListener(sListener);
//        }

        //下载封面
        downloadCover(bean);

        //下载歌曲
        int taskid = downloadManager.add(downloadRequest);

        bean.location = getDownloadPath();
        bean.fileName = destinationUri.getLastPathSegment();
        bean.progress = 0;
        bean.downloadStats = Music.DL_DOING;
        mDownloads.put(taskid, bean);
        mNewList = new ArrayList<>(mDownloads.values());
        mAllList.removeAll(mNewList);
        mAllList.addAll(mNewList);

        ZlDownloadEvent event = new ZlDownloadEvent();
        event.status = Music.DL_INIT;
        event.progress = 0;
        event.bean = bean;
        EventBus.getDefault().postSticky(event);

    }


    private void downloadCover(final Music music) {
        final File cover = new File(getCoverPath(), music.getValidCoverName());
        if (!cover.exists() && music.image != null) {
            Glide.with(MusicApp.sContext).asBitmap().load(music.image).into(new SimpleTarget<Bitmap>(Utils.dip2px(MusicApp.sContext, 60), Utils.dip2px(MusicApp.sContext, 60)) {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    saveImage(resource, cover);
                }
            });
        }
    }

    private void saveImage(Bitmap image, File imageFile) {
        try {
            OutputStream fOut = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.close();

            // Add the image to the system gallery
            galleryAddPic(imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void galleryAddPic(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        MusicApp.sContext.sendBroadcast(mediaScanIntent);
    }


    DownloadStatusListenerV1 sListener = new DownloadStatusListenerV1() {
        @Override
        public void onDownloadComplete(DownloadRequest downloadRequest) {
            Music bean = mDownloads.get(downloadRequest.getDownloadId());
            if (bean == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String path = bean.location + "/" + bean.fileName;
                File file = new File(path);
                if (file.exists()) {
                    new Thread(() -> {
                        Uri uri = FileUtil.copyUri(file, file.getName());
                        if (uri != null) {
                            bean.location = uri.toString();
                            downDone(bean);
                        } else {
                            downError(bean, downloadRequest);
                        }
                    }).start();
                }
            } else {
                downDone(bean);
            }
        }

        private void downDone(Music bean) {
            bean.downloadStats = Music.DL_DONE;
            mNewList = new ArrayList<Music>(mDownloads.values());
            try {
                mAllList.removeAll(mNewList);
            } catch (Exception e) {
                mAllList.clear();
            }

            mAllList.addAll(mNewList);
            vPrefsUtils.setDownloadCache(mAllList);

            vPrefsUtils.sDownloadSuccessCount++;
            if (bean.channel != Music.CHANNEL_JAMENDO) {
                ApiConstants.sDownloadQuota--;
                ApiConstants.rewarded = false;
            }
            FlurryEventReport.logDownloadSucc(bean.channel);

            ZlDownloadEvent event = new ZlDownloadEvent();
            event.status = Music.DL_DONE;
            event.progress = 100;
            event.bean = bean;
            EventBus.getDefault().postSticky(event);
        }


        @Override
        public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
            Music bean = mDownloads.get(downloadRequest.getDownloadId());
            if (bean == null) {
                return;
            }
            downError(bean, downloadRequest);
        }

        private void downError(Music bean, DownloadRequest downloadRequest) {
            bean.downloadStats = Music.DL_ERROR;

            mNewList = new ArrayList<Music>(mDownloads.values());
            mAllList.removeAll(mNewList);
            mAllList.addAll(mNewList);
            vPrefsUtils.setDownloadCache(mAllList);

            FlurryEventReport.logDownloadFail(bean.channel);

            ZlDownloadEvent event = new ZlDownloadEvent();
            event.status = Music.DL_ERROR;
            event.progress = 0;
            event.bean = mDownloads.get(downloadRequest.getDownloadId());
            EventBus.getDefault().postSticky(event);
        }

        @Override
        public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
            if (mDownloads.get(downloadRequest.getDownloadId()) == null) {
                return;
            }
            if (progress <= mDownloads.get(downloadRequest.getDownloadId()).progress) {
                return;
            }
            mDownloads.get(downloadRequest.getDownloadId()).downloadStats = Music.DL_DOING;
            mDownloads.get(downloadRequest.getDownloadId()).progress = progress;

            Music bean = mDownloads.get(downloadRequest.getDownloadId());
            ZlDownloadEvent event = new ZlDownloadEvent();
            event.status = Music.DL_DOING;
            event.progress = progress;
            event.soBytes = (int) (downloadedBytes - bean.soFarBytes);
            event.bean = bean;
            bean.soFarBytes = (int) downloadedBytes;
            EventBus.getDefault().postSticky(event);
        }
    };

    public String getDownloadPath() {
        return getDownloadPath(true);
    }

    public String getDownloadPath(boolean absolute) {
        File dir = new File(Environment.getExternalStorageDirectory(), folder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File[] files = MusicApp.getInstance().getExternalMediaDirs();
            if (files != null && files.length > 0) {
                File file = files[0];
                dir = new File(file, folder);
            } else {
                File musicFile = MusicApp.getInstance().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                if (musicFile != null) {
                    dir = new File(musicFile, folder);
                }
            }
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
        return absolute ? dir.getAbsolutePath() : folder;
    }

    public String getCoverPath() {
        String folder = "musiccover";
        File dir = new File(Environment.getExternalStorageDirectory(), folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    /**
     * 倒序排列
     *
     * @return
     */
    public ArrayList<Music> getReverseAllTasks() {
        if (null == mAllList) {
            return new ArrayList<>();
        }
        ArrayList<Music> temp = new ArrayList<>();
        temp.addAll(mAllList);
        Collections.reverse(temp);
        return temp;
    }

    public void removeTask(Music bean) {
        if (null != mAllList) {
            mAllList.remove(bean);
        }
        for (Iterator<Map.Entry<Integer, Music>> it = mDownloads.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Music> item = it.next();
            if (item.getValue().equals(bean)) {
                downloadManager.cancel(item.getKey());
                it.remove();
                break;
            }
        }
    }

    public List<Music> getAllTasks() {
        if (null == mAllList) {
            return new ArrayList<>();
        }
        return mAllList;
    }

    public void updateMuiscList(List<Music> musicList) {
        if (musicList == null || musicList.isEmpty()) {
            return;
        }
        mAllList.removeAll(musicList);
        mAllList.addAll(musicList);
        vPrefsUtils.setDownloadCache(mAllList);
        ScanEvent event = new ScanEvent(ScanEvent.SCAN_DONE);
        EventBus.getDefault().postSticky(event);
    }
}

