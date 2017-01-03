package com.newtech.jobnow.common;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * Created by SANG on 8/21/2016.
 */
public class FunctionCommon {
    private static final String TAG = FunctionCommon.class.getSimpleName();

    public static String hashString(String original) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(original.getBytes());
            byte[] digest = md.digest();

            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                stringBuffer.append(Integer.toString((digest[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            Log.d(TAG, "UTF-8 should always be supported", e);
            return "";
        }
    }
}
