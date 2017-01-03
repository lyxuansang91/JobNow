package com.newtech.jobnow.acitvity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.common.APICommon.JobNowService;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.models.LoginResponse;
import com.newtech.jobnow.models.TokenRequest;
import com.newtech.jobnow.utils.TypefaceUtil;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
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

    public void getApiToken() {
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        String email = sharedPreferences.getString(Config.KEY_EMAIL, "");
        Call<LoginResponse> call = service.getToken(new TokenRequest(userId, email));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Response<LoginResponse> response, Retrofit retrofit) {
                Log.d(TAG, "get token: " + response.body());
                if(response.body() != null && response.body().code == 200) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Config.KEY_TOKEN, response.body().result.apiToken);
                    editor.commit();
                } else
                    Toast.makeText(getApplicationContext(), response.body().message,
                            Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_connect, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void getApiToken(final TokenCallback callback) {
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        String email = sharedPreferences.getString(Config.KEY_EMAIL, "");
        Call<LoginResponse> call = service.getToken(new TokenRequest(userId, email));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Response<LoginResponse> response, Retrofit retrofit) {
                Log.d(TAG, "get token: " + response.body());
                if(response.body() != null && response.body().code == 200) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Config.KEY_TOKEN, response.body().result.apiToken);
                    editor.commit();
                    callback.onTokenSuccess();
                } else
                    Toast.makeText(getApplicationContext(), response.body().message,
                            Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_connect, Toast.LENGTH_SHORT)
                        .show();
            }
        });
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

    public interface TokenCallback {
        public void onTokenSuccess();
    }
}
