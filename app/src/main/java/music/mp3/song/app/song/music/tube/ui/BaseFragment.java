package music.mp3.song.app.song.music.tube.ui;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import music.mp3.song.app.song.music.tube.bean.BaseBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public abstract class BaseFragment extends Fragment {
    //是否第一次进来
    private boolean mInitialized;


    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化view监听
     */
    protected abstract void initView(View parentView, Bundle savedInstanceState);

    /**
     * 初始化view监听
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     * {@link #onLazyLoad()} 二选一
     */
    protected void initDatas() {
    }

    /**
     * 是否开启接收eventbus事件 默认不开启
     */
    protected boolean isStartEventBus() {
        return false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isStartEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mInitialized) {
            if (getUserVisibleHint()) {
                onLazyLoad();
            }
        }
    }

    /**
     * 懒加载回调 和 {@link #initDatas()} 二选一
     */
    protected void onLazyLoad() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (!mInitialized) {
            initView(view, savedInstanceState);
            initListener();
            if (getUserVisibleHint()) {
                onLazyLoad();
            }
            initDatas();
            mInitialized = true;
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(BaseBean event) {
    }

    protected boolean unsafe() {
        return getContext() == null || getActivity() == null
                || isDetached() || isRemoving() || !isAdded();
    }

    @Override
    public void onDestroy() {
        if (isStartEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
}
