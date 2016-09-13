package com.shane.powersaver.util;

import android.content.Context;


import java.io.IOException;
import java.io.InputStream;

public class AssetUtils {

    /**
     * Returns the contents of 'fileName' from asset as a string
     * 
     * @param context
     * @param fileName
     * @return
     */
    public static String getStrFromAssetFile(Context context, String fileName) {
        InputStream in = null;
        try {
            in = context.getAssets().open(fileName);
            return IOUtils.toString(in);
        } catch (IOException e) {
        } finally {
            IOUtils.closeQuietly(in);
        }
        return null;
    }
}
