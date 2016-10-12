package com.shane.powersaver.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;

import com.shane.powersaver.R;
import com.shane.powersaver.bean.Banner;
import com.shane.powersaver.util.UIHelper;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class ViewNewsBanner extends RelativeLayout implements View.OnClickListener {
    private Banner banner;
    private ImageView iv_banner;
    //private TextView tv_title;

    public ViewNewsBanner(Context context) {
        super(context, null);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_news_banner, this, true);
        iv_banner = (ImageView) findViewById(R.id.iv_banner);
        //tv_title = (TextView) findViewById(R.id.tv_title);
        setOnClickListener(this);
    }

    public void initData(RequestManager manager, Banner banner) {
        this.banner = banner;
        //tv_title.setText(banner.getName());
        manager.load(banner.getImg()).into(iv_banner);
    }

    @Override
    public void onClick(View v) {

//        UIHelper.showBannerDetail(getContext(), banner);
    }

    public String getTitle() {
        return banner.getName();
    }
}
