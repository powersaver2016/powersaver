package com.shane.powersaver.adapter.general;

import android.content.pm.PackageManager;
import android.widget.ProgressBar;

import com.shane.powersaver.R;
import com.shane.powersaver.adapter.ViewHolder;
import com.shane.powersaver.adapter.base.BaseListAdapter;
import com.shane.powersaver.bean.base.State;

/**
 * Created by shane on 16-8-25.
 */
public class CpuStateAdapter extends BaseListAdapter<State> {
    private String systemTime;

    public CpuStateAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, State item, int position) {
        vh.setText(android.R.id.title, item.getName());

        double percent = item.getRatio();
        vh.setText(android.R.id.text1, String.format("%.1f%%", percent));

        ProgressBar progressBar = (ProgressBar)vh.getView(android.R.id.progress);
        progressBar.setMax(100);
        progressBar.setProgress((int)Math.round(percent));

        vh.setImage(android.R.id.icon, R.drawable.cpu);
    }

    @Override
    protected int getLayoutId(int position, State item) {
        return R.layout.item_list_power_rank;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }
}
