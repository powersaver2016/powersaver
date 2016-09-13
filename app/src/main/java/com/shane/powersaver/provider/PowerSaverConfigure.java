package com.shane.powersaver.provider;

import android.net.Uri;

public class PowerSaverConfigure {
    /**
     * 授权管理ContentProvider使用的Authority
     */
    public static final String AUTHORITY = "com.shane.powersaver.configure";
    /**
     * 授权管理ContentProvider的默认URI
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

}
