package com.shane.powersaver.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.shane.android.common.utils.SysUtils;
import com.shane.powersaver.AppContext;
import com.shane.powersaver.R;
import com.shane.powersaver.bean.base.Constants;
import com.shane.powersaver.bean.kernel.SummaryStatsDumpsys;
import com.shane.powersaver.bean.kernel.WakeupSources;
import com.shane.powersaver.cloudcontrol.CloudUpdateReceiver;
import com.shane.powersaver.interf.BaseViewInterface;
import com.shane.powersaver.interf.OnTabReselectListener;
import com.shane.powersaver.util.AlarmHelper;
import com.shane.powersaver.util.LogUtil;
import com.shane.powersaver.util.RootShell;
import com.shane.powersaver.widget.MyFragmentTabHost;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * LogUtil
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07 18:00
 */
public class MainActivity extends AppCompatActivity implements
        TabHost.OnTabChangeListener, BaseViewInterface, View.OnClickListener,
        View.OnTouchListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private long mBackPressedTime;

    @Bind(android.R.id.tabhost)
    MyFragmentTabHost mTabHost;

    /**
     * Used to store the last screen title. For use in
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Bind(R.id.quick_option_iv)
    View mAddBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_main);
        ButterKnife.bind(this);
        LogUtil.i(TAG, "phoneRooted: " + RootShell.getInstance().phoneRooted());
        LogUtil.i(TAG, "hasRootPermissions: " + RootShell.getInstance().hasRootPermissions());
        AppContext.DEBUG = true;
//        WakeupSources.parseWakeupSources(this);
//        SummaryStatsDumpsys.getSummaryStats(true, true, true);
//        List<String> res = RootShell.getInstance().run("dumpsys batterystats");
//        for (String item : res) {
//            LogUtil.i(TAG, item);
//        }
//        LogUtil.i(TAG, "SIZE:" + res.size());
        // show install as system app screen if root available but perms missing
        if (RootShell.getInstance().hasRootPermissions() && !SysUtils.hasBatteryStatsPermission(this)) {
            // attempt to set perms using pm-comand
            Log.i(TAG, "attempting to grant perms with 'pm grant'");

            String pkg = this.getPackageName();
            RootShell.getInstance().run("pm grant " + pkg + " android.permission.BATTERY_STATS");

            Toast.makeText(this, getString(R.string.info_deleting_refs), Toast.LENGTH_SHORT).show();
            if (SysUtils.hasBatteryStatsPermission(this)) {
                Log.i(TAG, "succeeded");
            } else {
                Log.i(TAG, "failed");
            }
        }

//        this.sendBroadcast(new Intent(CloudUpdateReceiver.ACTION_UPDATE_DATA).setPackage(Constants.PACKAGE_NAME));
//        this.sendBroadcast(new Intent(CloudUpdateReceiver.ACTION_UPDATE_DATA));
//        PendingIntent pi = AlarmHelper.getInstance(this).getPendingIntent(CloudUpdateReceiver.ACTION_UPDATE_DATA, AlarmHelper.TYPE_BROADCAST);
//        AlarmHelper.getInstance(this).scheduleElapsedAlarm(pi, 10*1000, true);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initView() {
        mTitle = getTitle();
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }

        initTabs();

        // 中间按键图片触发
        mAddBt.setOnClickListener(this);

        mTabHost.setCurrentTab(0);
        mTabHost.setOnTabChangedListener(this);
    }

    @SuppressWarnings("deprecation")
    private void initTabs() {
        MainTab[] tabs = MainTab.values();
        int size = tabs.length;
        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];
            TabHost.TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()));
            View indicator = View.inflate(this, R.layout.tab_indicator, null);
            TextView title = (TextView) indicator.findViewById(R.id.tab_title);
            ImageView icon = (ImageView) indicator.findViewById(R.id.iv_icon);

            Drawable drawable = this.getResources().getDrawable(mainTab.getResIcon());
            icon.setImageDrawable(drawable);
            //title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            if (i == 2) {
                indicator.setVisibility(View.INVISIBLE);
                mTabHost.setNoTabChangedTag(getString(mainTab.getResName()));
            }
            title.setText(getString(mainTab.getResName()));
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            mTabHost.addTab(tab, mainTab.getClz(), null);

            if (mainTab.equals(MainTab.ME)) {
                View cn = indicator.findViewById(R.id.tab_mes);
//                mBvNotice = new BadgeView(MainActivity.this, cn);
//                mBvNotice.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//                mBvNotice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//                mBvNotice.setBackgroundResource(R.drawable.notification_bg);
//                mBvNotice.setGravity(Gravity.CENTER);
            }
            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }
    }

    @Override
    public void initData() {
    }

    @SuppressWarnings("deprecation")
    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        final int size = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View v = mTabHost.getTabWidget().getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                v.setSelected(true);
            } else {
                v.setSelected(false);
            }
        }
        if (tabId.equals(getString(MainTab.ME.getResName()))) {
//            mBvNotice.setText("");
//            mBvNotice.hide();
        }
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            // 点击了快速操作按钮
            case R.id.quick_option_iv:
                showQuickOption();
                break;
            default:
                break;
        }
    }

    // 显示快速操作界面
    private void showQuickOption() {
//        final QuickOptionDialog dialog = new QuickOptionDialog(
//                MainActivity.this);
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouchEvent(event);
        boolean consumed = false;
        // use getTabHost().getCurrentTabView to decide if the current tab is
        // touched again
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && v.equals(mTabHost.getCurrentTabView())) {
            // use getTabHost().getCurrentView() to get a handle to the view
            // which is displayed in the tab - and to get this views context
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment != null
                    && currentFragment instanceof OnTabReselectListener) {
                OnTabReselectListener listener = (OnTabReselectListener) currentFragment;
                listener.onTabReselect();
                consumed = true;
            }
        }
        return consumed;
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(
                mTabHost.getCurrentTabTag());
    }

    @Override
    public void onBackPressed() {

        boolean isDoubleClick = true;

        if (isDoubleClick) {
            long curTime = SystemClock.uptimeMillis();
            if ((curTime - mBackPressedTime) < (3 * 1000)) {
                finish();
            } else {
                mBackPressedTime = curTime;
                Toast.makeText(this, R.string.tip_double_click_exit, Toast.LENGTH_LONG).show();
            }
        } else {
            finish();
        }

    }
}
