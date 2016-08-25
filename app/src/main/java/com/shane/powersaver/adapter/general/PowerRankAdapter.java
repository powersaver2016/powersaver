package com.shane.powersaver.adapter.general;

import com.shane.powersaver.R;
import com.shane.powersaver.adapter.ViewHolder;
import com.shane.powersaver.adapter.base.BaseListAdapter;
import com.shane.powersaver.bean.base.BatterySipper;
import com.shane.powersaver.util.StringUtils;

/**
 * Created by shane on 16-8-25.
 */
public class PowerRankAdapter extends BaseListAdapter<BatterySipper> {
    private String systemTime;

    public PowerRankAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, BatterySipper item, int position) {
        vh.setText(R.id.tv_title, item.getName());

        vh.setTextColor(R.id.tv_title, mCallback.getContext().getResources().getColor(R.color.blog_title_text_color_light));
        vh.setTextColor(R.id.tv_description, mCallback.getContext().getResources().getColor(R.color.ques_bt_text_color_dark));

        vh.setText(R.id.tv_description, item.getData(0));
        vh.setText(R.id.tv_time, StringUtils.friendly_time(StringUtils.getCurTimeStr()));
        vh.setText(R.id.tv_comment_count, String.valueOf(item.totalPowerMah));
        vh.setImage(R.id.iv_today, R.drawable.ic_label_today);
        vh.setVisibility(R.id.iv_today);
    }

    @Override
    protected int getLayoutId(int position, BatterySipper item) {
        return R.layout.item_list_power_rank;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }
}
