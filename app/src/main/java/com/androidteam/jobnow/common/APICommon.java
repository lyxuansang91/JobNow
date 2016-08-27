package com.androidteam.jobnow.common;

import android.util.Log;

import com.androidteam.jobnow.models.IndustryResponse;
import com.androidteam.jobnow.models.JobListReponse;
import com.androidteam.jobnow.models.JobLocationResponse;
import com.androidteam.jobnow.models.LoginRequest;
import com.androidteam.jobnow.models.LoginResponse;
import com.androidteam.jobnow.models.RegisterFBReponse;
import com.androidteam.jobnow.models.RegisterFBRequest;
import com.androidteam.jobnow.models.RegisterRequest;
import com.androidteam.jobnow.models.RegisterResponse;
import com.androidteam.jobnow.models.SkillResponse;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.Url;

/**
 * Created by SANG on 8/21/2016.
 */
public class APICommon {
    public static final String BASE_URL = "http://jobnew.vnblues.net/api/v1/";
    public static final int ANDROID = 4;
    public static final int IOS = 3;
    public static final int WEB = 2;
    private static final String TAG = APICommon.class.getSimpleName();

    public static String getApiKey() {
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
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return null;
        }
    }

    public interface JobNowService {
        @POST("users/postLogin")
        Call<LoginResponse> loginUser(@Body LoginRequest request);

        @POST("users/postRegister")
        Call<RegisterResponse> registerUser(@Body RegisterRequest request);

        @POST("users/postRegisterSocialite")
        Call<RegisterFBReponse> registerFB(@Body RegisterFBRequest request);

        @GET()
        Call<JobListReponse> getJobList(@Url String url, @Query("page") int page, @Query("Order") String Order, @Query("Title") String Title, @Query("Location") String Location, @Query("Skill") String Skill, @Query("MinSalary") double MinSalary, @Query("FromSalary") double FromSalary, @Query("ToSalary") double ToSalary);

        @GET()
        Call<JobListReponse> getJobList(@Url String url);

        @GET()
        Call<IndustryResponse> getIndustry(@Url String url);

        @GET
        Call<JobLocationResponse> getJobLocation(@Url String url);

        @GET
        Call<SkillResponse> getSkill(@Url String url);
    }
}
