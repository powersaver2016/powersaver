package com.shane.powersaver.adapter.general;

import com.shane.powersaver.AppContext;
import com.shane.powersaver.R;
import com.shane.powersaver.adapter.ViewHolder;
import com.shane.powersaver.adapter.base.BaseListAdapter;
import com.shane.powersaver.bean.news.News;
import com.shane.powersaver.fragment.general.NewsFragment;
import com.shane.powersaver.util.StringUtils;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class NewsAdapter extends BaseListAdapter<News> {
    private String systemTime;
    public NewsAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, News item, int position) {
        vh.setText(R.id.tv_title, item.getTitle());

        if (AppContext.isOnReadedPostList(NewsFragment.HISTORY_NEWS, item.getId() + "")) {
            vh.setTextColor(R.id.tv_title, mCallback.getContext().getResources().getColor(R.color.count_text_color_light));
            vh.setTextColor(R.id.tv_description, mCallback.getContext().getResources().getColor(R.color.count_text_color_light));
        } else {
            vh.setTextColor(R.id.tv_title, mCallback.getContext().getResources().getColor(R.color.blog_title_text_color_light));
            vh.setTextColor(R.id.tv_description, mCallback.getContext().getResources().getColor(R.color.ques_bt_text_color_dark));
        }

        vh.setText(R.id.tv_description, item.getBody());
        vh.setText(R.id.tv_time, StringUtils.friendly_time(item.getPubDate()));
        vh.setText(R.id.tv_comment_count, String.valueOf(item.getCommentCount()));
        if(StringUtils.isSameDay(systemTime,item.getPubDate())){
            vh.setImage(R.id.iv_today, R.drawable.ic_label_today);
            vh.setVisibility(R.id.iv_today);
        }else {
            vh.setGone(R.id.iv_today);
        }
    }

    @Override
    protected int getLayoutId(int position, News item) {
        return R.layout.item_list_news;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }
}
