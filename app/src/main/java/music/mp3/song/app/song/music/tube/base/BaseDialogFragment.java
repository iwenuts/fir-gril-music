package music.mp3.song.app.song.music.tube.base;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import music.mp3.song.app.song.music.tube.R;

public abstract class BaseDialogFragment extends DialogFragment {

    private static final float DEFAULT_DIM = 0.5f;

    private DismissListener dismissListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, getTheme());
    }

    /**
     * 如果需要设置主题，请重写这个函数，并返回一个主题
     */
    @Override
    public int getTheme() {
        return R.style.ViewsBaseDialogFragment;
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window mWindow = getDialog().getWindow();
        WindowManager.LayoutParams mLayoutParams = mWindow.getAttributes();
        mLayoutParams.width = dip2px(getContext(), 300);
        mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(mLayoutParams);
    }

    /**
     * dialog 非内容区域的黑色透明度，默认 0.8
     */
    protected float getDimAmount() {
        return DEFAULT_DIM;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutRes(), container, false);
        bindView(v);
        return v;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Deprecated
    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    /**
     * 安全的显示 dialog
     */
    public void showAllowingStateLoss(FragmentManager fmgr) {
        show(fmgr, getFragmentTag());
    }

    /**
     * 安全的显示 dialog
     */
    public void show(FragmentManager fmgr) {
        show(fmgr, getFragmentTag());
    }

    @LayoutRes
    public abstract int getLayoutRes();

    public abstract void bindView(View v);


    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

    public void setOnDismissListener(DismissListener listener) {
        dismissListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (null != dismissListener) {
            dismissListener.onDismiss();
        }
    }

    public interface DismissListener {
        void onDismiss();
    }

    public String getFragmentTag() {
        return "dialog_" + getIdentityString();
    }

    protected String getIdentityString() {
        return Integer.toHexString(System.identityHashCode(this));
    }
}

