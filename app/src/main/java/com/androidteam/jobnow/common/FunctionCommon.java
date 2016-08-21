package com.androidteam.jobnow.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by SANG on 8/21/2016.
 */
public class FunctionCommon {
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
}
