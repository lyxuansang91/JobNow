package com.androidteam.jobnow.acitvity;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;

import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.common.APICommon.JobNowService;
import com.androidteam.jobnow.utils.TypefaceUtil;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by manhi on 10/8/2016.
 */
public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication _instance = null;

    private JobNowService _service;

    public static MyApplication getInstance() {
        return _instance;
    }

    public MyApplication() {

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

    public JobNowService getJobNowService() {
        return _service;
    }

    @Override
    public void onCreate() {
        _instance = this;
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "helveticaneue.ttf");
        changeAppFont();
        initAPIService();
    }

    private void initAPIService() {
        final Gson gson = new GsonBuilder().serializeNulls().create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.interceptors().add(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APICommon.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();


        _service = retrofit.create(JobNowService.class);
    }

    private void changeAppFont() {

        setDefaultFont(this, "DEFAULT", "helveticaneue.ttf");
        setDefaultFont(this, "DEFAULT_BOLD", "helveticaneuelight.ttf");
//
//        setDefaultFont(this, "MONOSPACE", "fonts/FuturaLT-Oblique.ttf");
//        setDefaultFont(this, "SANS_SERIF", "fonts/FuturaLT.ttf");
        setDefaultFont(this, "SERIF", "helveticaneue.ttf");
    }
}
