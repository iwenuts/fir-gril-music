package music.mp3.song.app.song.music.tube.ui;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import music.mp3.song.app.song.music.tube.bean.BaseBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public abstract class BaseActivity extends AppCompatActivity {

    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 1.获取get Intent数据 2.状态保存数据读取
     *
     * @param savedInstanceState
     */
    protected void initBundleExtra(Bundle savedInstanceState) {
    }

    /**
     * 初始化UI view
     */
    protected abstract void initViews();

    /**
     * 初始化view监听
     */
    protected void initListeners() {
    }

    /**
     * 初始化数据
     */
    protected abstract void initDatas();

    /**
     * 是否开启接收eventbus事件 默认开启:接收重新登录事件
     *
     * @return
     */
    protected boolean isStartEventBus() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        if (bundle != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            bundle.remove(FRAGMENTS_TAG);
        }
        super.onCreate(bundle);
        setContentView(getLayoutId());
        if (isStartEventBus()) {
            EventBus.getDefault().register(this);
        }
        initBundleExtra(bundle);
        initViews();
        initListeners();
        initDatas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isStartEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BaseBean event) {

    }

    @Override
    public File getExternalFilesDir(String type) {
        if (super.getExternalFilesDir(type) == null) {
            File tempFileDir = new File(getFilesDir(), "newSDCard");
            if (!tempFileDir.exists()) {
                tempFileDir.mkdirs();
            }
            return tempFileDir;
        }
        return super.getExternalFilesDir(type);
    }

    @Override
    public File getExternalCacheDir() {
        if (super.getExternalCacheDir() == null) {
            File tempFileDir = new File(getCacheDir(), "newSDCard");
            if (!tempFileDir.exists()) {
                tempFileDir.mkdirs();
            }
            return tempFileDir;
        }
        return super.getExternalCacheDir();
    }

}
