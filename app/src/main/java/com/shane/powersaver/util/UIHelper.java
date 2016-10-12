package com.shane.powersaver.util;

import android.content.Context;
import android.os.Bundle;

import com.shane.powersaver.AppContext;


/**
 * 界面帮助类
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-22
 */
public class UIHelper {

    /**
     * 显示用户中心页面
     *
     * @param context
     * @param hisuid
     * @param hisuid
     * @param hisname
     */
    public static void showUserCenter(Context context, int hisuid,
                                      String hisname) {
        if (hisuid == 0 && hisname.equalsIgnoreCase("匿名")) {
            AppContext.showToast("提醒你，该用户为非会员");
            return;
        }
        Bundle args = new Bundle();
        args.putInt("his_id", hisuid);
        args.putString("his_name", hisname);
//        showSimpleBack(context, SimpleBackPage.USER_CENTER, args);
    }
}
