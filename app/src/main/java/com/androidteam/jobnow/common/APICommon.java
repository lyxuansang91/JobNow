package com.androidteam.jobnow.common;

import android.util.Log;

import com.androidteam.jobnow.models.LoginRequest;
import com.androidteam.jobnow.models.LoginResponse;
import com.androidteam.jobnow.models.RegisterResponse;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by SANG on 8/21/2016.
 */
public class APICommon {
    public static final String BASE_URL = "http://jobnew.vnblues.net/api/v1/";
    public static final int ANDROID = 4;
    public static final int IOS = 3;
    public static final int WEB = 2;
    private static final String TAG = APICommon.class.getSimpleName();

    public static String getApiKey(){
        return "tD0EudnC92D198TR";
    }

    public static String getAppId() {
        return "com.jobnow.demo";
    }

    public static int getDeviceType() {
        return ANDROID;
    }

    public static String getSign(String api_key, String path_url) {
        try {
            long time = System.currentTimeMillis();
            StringBuilder builder = new StringBuilder();
            builder.append(time);
            builder.append(".");
            String md5_hash = FunctionCommon.hashString(path_url + ":" + time + ":" + api_key);
            builder.append(md5_hash);
            return builder.toString();
        } catch(Exception ex) {
            Log.e(TAG, ex.getMessage());
            return null;
        }
    }

    public interface JobNowService {
        @POST("users/postLogin")
        Call<LoginResponse> loginUser(@Body LoginRequest request);
        Call<RegisterResponse> registerUser(@Body RegisterRequest request);
    }
}
