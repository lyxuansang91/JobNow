package com.androidteam.jobnow.acitvity;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import com.androidteam.jobnow.utils.TypefaceUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by manhi on 10/8/2016.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "helveticaneue.ttf");
        changeAppFont();
    }
    private void changeAppFont() {

        setDefaultFont(this, "DEFAULT", "helveticaneue.ttf");
        setDefaultFont(this, "DEFAULT_BOLD", "helveticaneuelight.ttf");
//
//        setDefaultFont(this, "MONOSPACE", "fonts/FuturaLT-Oblique.ttf");
//        setDefaultFont(this, "SANS_SERIF", "fonts/FuturaLT.ttf");
        setDefaultFont(this, "SERIF", "helveticaneue.ttf");
    }


    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        if (isVersionGreaterOrEqualToLollipop()) {
            Map<String, Typeface> newMap = new HashMap<String, Typeface>();
            newMap.put("sans-serif", newTypeface);
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField(staticTypefaceFieldName);
                staticField.setAccessible(true);
                staticField.set(null, newTypeface);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean isVersionGreaterOrEqualToLollipop() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        return false;
    }
}
