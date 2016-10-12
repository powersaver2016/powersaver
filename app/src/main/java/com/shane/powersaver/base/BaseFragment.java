package com.shane.powersaver.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shane.powersaver.AppContext;
import com.shane.powersaver.R;
import com.shane.powersaver.interf.BaseFragmentInterface;
import com.shane.powersaver.ui.dialog.DialogControl;

/**
 * 碎片基类
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 *
 */

public class BaseFragment extends Fragment implements
        View.OnClickListener, BaseFragmentInterface {
    protected String TAG = BaseFragment.class.getSimpleName();

    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static final int STATE_NOMORE = 3;
    public static final int STATE_PRESSNONE = 4;// 正在下拉但还没有到刷新的状态
    public static int mState = STATE_NONE;

    protected LayoutInflater mInflater;

    protected static final int MSG_GET_DATA = 1;
    protected static final int MSG_UPDATE_DATA = 1;

    protected BackgroundHandler mBackgroundHandler;
    protected UiHandler mUiHandler;

    protected void doInBackground(Message msg){ }
    protected void doInMainThread(Message msg){ }

    protected final class BackgroundHandler extends Handler {
        BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            doInBackground(msg);
        }
    }

    protected final class UiHandler extends Handler {
        UiHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            doInMainThread(msg);
        }
    }

    public AppContext getApplication() {
        return (AppContext) getActivity().getApplication();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread thread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Looper looper = thread.getLooper();
        mBackgroundHandler = new BackgroundHandler(looper);
        mUiHandler = new UiHandler(this.getActivity().getMainLooper());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.mInflater = inflater;
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mBackgroundHandler != null) {
            mBackgroundHandler.removeCallbacksAndMessages(null);
            mBackgroundHandler.getLooper().quit();
            mBackgroundHandler = null;
        }

        super.onDestroy();
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View inflateView(int resId) {
        return this.mInflater.inflate(resId, null);
    }

    public boolean onBackPressed() {
        return false;
    }

    protected void hideWaitDialog() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DialogControl) {
            ((DialogControl) activity).hideWaitDialog();
        }
    }

    protected ProgressDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    protected ProgressDialog showWaitDialog(int resid) {
        FragmentActivity activity = getActivity();
        if (activity instanceof DialogControl) {
            return ((DialogControl) activity).showWaitDialog(resid);
        }
        return null;
    }

    protected ProgressDialog showWaitDialog(String str) {
        FragmentActivity activity = getActivity();
        if (activity instanceof DialogControl) {
            return ((DialogControl) activity).showWaitDialog(str);
        }
        return null;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
