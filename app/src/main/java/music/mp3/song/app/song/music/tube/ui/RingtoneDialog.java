package music.mp3.song.app.song.music.tube.ui;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.R;
import music.mp3.song.app.song.music.tube.bean.Music;
import music.mp3.song.app.song.music.tube.ztools.PermissionUtils;
import music.mp3.song.app.song.music.tube.ztools.ToastUtils;

import java.io.File;


public class RingtoneDialog extends Dialog {
    TextView titleTv;
    TextView phoneTv, alarmTv, notifiTv;
    Music mBean;
    Uri mUri;

    public RingtoneDialog(Context context) {
        // 在构造方法里, 传入主题
        super(context, R.style.BottomDialogStyle);
        // 拿到Dialog的Window, 修改Window的属性
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bottom_ringtone);
        titleTv = findViewById(R.id.title_tv);
        phoneTv = findViewById(R.id.phone_tv);
        alarmTv = findViewById(R.id.alarm_tv);
        notifiTv = findViewById(R.id.notification_tv);
        phoneTv.setTag(mBean);
        phoneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtils.checkWritePermission(v.getContext())) {
                    return;
                }
                try {
                    Uri uri = getUri(1);
                    if (null != uri) {
                        RingtoneManager.setActualDefaultRingtoneUri(v.getContext(), RingtoneManager.TYPE_RINGTONE, uri);
                        ToastUtils.showShortToast(R.string.set_ringtone_success);
                    } else {
                        ToastUtils.showShortToast(R.string.set_error);
                    }
                } catch (Throwable e) {

                }

                dismissDialog();

            }
        });
        alarmTv.setTag(mBean);
        alarmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtils.checkWritePermission(v.getContext())) {
                    return;
                }
                try {
                    Uri uri = getUri(3);
                    if (null != uri) {
                        RingtoneManager.setActualDefaultRingtoneUri(v.getContext(), RingtoneManager.TYPE_ALARM, uri);
                        ToastUtils.showShortToast(R.string.set_alarm_success);
                    } else {
                        ToastUtils.showShortToast(R.string.set_error);
                    }
                } catch (Throwable e) {

                }

                dismissDialog();
            }
        });
        notifiTv.setTag(mBean);
        notifiTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtils.checkWritePermission(v.getContext())) {
                    return;
                }
                try {
                    Uri uri = getUri(2);
                    if (null != uri) {
                        RingtoneManager.setActualDefaultRingtoneUri(v.getContext(), RingtoneManager.TYPE_NOTIFICATION, getUri(2));
                        ToastUtils.showShortToast(R.string.set_notification_success);
                    } else {
                        ToastUtils.showShortToast(R.string.set_error);
                    }
                } catch (Throwable e) {

                }
                dismissDialog();
            }
        });
        initData();
    }

    private void initData() {
        if (null != mBean) {
            titleTv.setText("Set Rintone: " + mBean.getTitle());
        }
    }

    public void setBean(Music mBean) {
        this.mBean = mBean;

    }

    /**
     * @param type 1,2,3
     * @return
     */
    private Uri getUri(int type) {
        File f = new File(mBean.location, mBean.fileName);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Cursor cursor = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                cursor = MusicApp.getInstance().getContentResolver().query(uri,
                        new String[]{
                                BaseColumns._ID,
                                MediaStore.Audio.Media.IS_RINGTONE,
                                MediaStore.Audio.Media.IS_NOTIFICATION,
                                MediaStore.Audio.Media.IS_ALARM,
                        },
                        MediaStore.MediaColumns.TITLE + "=?",
                        new String[]{mBean.title}, null);
//                MediaStore.MediaColumns.TITLE + "=? AND " + MediaStore.MediaColumns.RELATIVE_PATH + " LIKE ?",
//                        new String[]{mBean.title, MyDownloadManager.folder}, null);
            } else {
                cursor = MusicApp.getInstance().getContentResolver().query(uri,
                        new String[]{
                                BaseColumns._ID,
                                MediaStore.Audio.Media.IS_RINGTONE,
                                MediaStore.Audio.Media.IS_NOTIFICATION,
                                MediaStore.Audio.Media.IS_ALARM,
                        },
                        MediaStore.MediaColumns.DATA + "=?",
                        new String[]{f.getAbsolutePath()}, null);
            }


            if (cursor != null && cursor.moveToFirst()) {
                values.put(MediaStore.Audio.Media.IS_RINGTONE, cursor.getInt(1) != 0);
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, cursor.getInt(2) != 0);
                values.put(MediaStore.Audio.Media.IS_ALARM, cursor.getInt(3) != 0);
                if (type == 1) {
                    values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                } else if (type == 2) {
                    values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                } else if (type == 3) {
                    values.put(MediaStore.Audio.Media.IS_ALARM, true);
                }
                MusicApp.getInstance().getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?", new String[]{f.getAbsolutePath()});
                return Uri.withAppendedPath(uri, "" + cursor.getLong(0));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return null;
                } else {
                    values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
                    values.put(MediaStore.MediaColumns.TITLE, mBean.title);
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
                    values.put(MediaStore.Audio.Media.ARTIST, mBean.artistName);
                    values.put(MediaStore.Audio.Media.DURATION, mBean.realduration);

                    if (type == 1) {
                        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                    } else if (type == 2) {
                        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                    } else if (type == 3) {
                        values.put(MediaStore.Audio.Media.IS_ALARM, true);
                    }
                    return MusicApp.getInstance().getContentResolver().insert(uri, values);
                }

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void dismissDialog() {
        dismiss();
    }


}
