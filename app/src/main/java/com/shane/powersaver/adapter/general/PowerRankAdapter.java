package com.shane.powersaver.adapter.general;

import android.content.pm.PackageManager;
import android.widget.ProgressBar;

import com.shane.powersaver.R;
import com.shane.powersaver.adapter.ViewHolder;
import com.shane.powersaver.adapter.base.BaseListAdapter;
import com.shane.powersaver.bean.base.BatterySipper;
import com.shane.powersaver.bean.base.Constants;
import com.shane.powersaver.bean.base.UidNameResolver;
import com.shane.powersaver.bean.kernel.BatterySipperResourceHelper;
import com.shane.powersaver.util.StringUtils;
import com.shane.powersaver.util.TextUtils;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class PowerRankAdapter extends BaseListAdapter<BatterySipper> {
    private String systemTime;

    public PowerRankAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, BatterySipper item, int position) {
        vh.setText(android.R.id.title, BatterySipperResourceHelper.getDisplayName(vh.getContext(), item));

        double percent = item.getRatio();
        vh.setText(android.R.id.text1, String.format("%.1f%%", percent));

        ProgressBar progressBar = (ProgressBar)vh.getView(android.R.id.progress);
        progressBar.setMax(100);
        progressBar.setProgress((int)Math.round(percent));

        int iconId = BatterySipperResourceHelper.getIconId(item);
        if (iconId > 0) {
            vh.setImage(android.R.id.icon, iconId);
        } else if (!TextUtils.isEmpty(item.getPackageName())) {
            vh.setImage(android.R.id.icon, UidNameResolver.getInstance(vh.getContext()).getIcon(item.getPackageName()));
        } else {
            PackageManager pm = vh.getContext().getPackageManager();
            vh.setImage(android.R.id.icon, pm.getDefaultActivityIcon());
        }
    }

    @Override
    protected int getLayoutId(int position, BatterySipper item) {
        return R.layout.item_list_power_rank;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }
}
