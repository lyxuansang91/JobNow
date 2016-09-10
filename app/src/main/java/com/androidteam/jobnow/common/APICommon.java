package com.androidteam.jobnow.common;

import android.util.Log;

import com.androidteam.jobnow.models.BaseResponse;
import com.androidteam.jobnow.models.DetailJobResponse;
import com.androidteam.jobnow.models.ExperienceRequest;
import com.androidteam.jobnow.models.ExperienceResponse;
import com.androidteam.jobnow.models.IndustryResponse;
import com.androidteam.jobnow.models.JobListReponse;
import com.androidteam.jobnow.models.JobLocationResponse;
import com.androidteam.jobnow.models.LoginRequest;
import com.androidteam.jobnow.models.LoginResponse;
import com.androidteam.jobnow.models.RegisterFBReponse;
import com.androidteam.jobnow.models.RegisterFBRequest;
import com.androidteam.jobnow.models.RegisterRequest;
import com.androidteam.jobnow.models.RegisterResponse;
import com.androidteam.jobnow.models.SkillRequest;
import com.androidteam.jobnow.models.SkillResponse;
import com.androidteam.jobnow.models.UpdateProfileRequest;
import com.androidteam.jobnow.models.UploadFileResponse;
import com.androidteam.jobnow.models.UserDetailResponse;
import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
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


        @GET("jobs/getAppliedJob/{sign}/{app_id}/{device_type}/{user_id}/{ApiToken}")
        Call<JobListReponse> getAppliedListJob(@Path("sign") String sign,
                                               @Path("app_id") String app_id,
                                               @Path("device_type") int device_type,
                                               @Path("user_id") int user_id,
                                               @Path("ApiToken") String apiToken,
                                               @Query("page") Integer page);

        @GET("jobs/getSaveJob/{sign}/{app_id}/{device_type}/{user_id}/{ApiToken}")
        Call<JobListReponse> getSaveListJob(@Path("sign") String sign,
                                            @Path("app_id") String app_id,
                                            @Path("device_type") int device_type,
                                            @Path("user_id") int user_id,
                                            @Path("ApiToken") String apiToken);

        @POST("users/postLogin")
        Call<LoginResponse> loginUser(@Body LoginRequest request);

        @POST("users/postRegister")
        Call<RegisterResponse> registerUser(@Body RegisterRequest request);

        @POST("users/postRegisterSocialite")
        Call<RegisterFBReponse> registerFB(@Body RegisterFBRequest request);

        @GET("jobs/getListJob/{sign}/{app_id}/{device_type}/")
        Call<JobListReponse> getJobListByParam(@Path("sign") String sign,
                                               @Path("app_id") String app_id,
                                               @Path("device_type") int device_type,
                                               @Query("page") Integer page,
                                               @Query("Order") String Order,
                                               @Query("Title") String Title,
                                               @Query("Location") String Location,
                                               @Query("Skill") String Skill,
                                               @Query("MinSalary") Integer MinSalary,
                                               @Query("FromSalary") Integer FromSalary,
                                               @Query("ToSalary") Integer ToSalary,
                                               @Query("IndustryID") Integer industryID);

        @GET()
        Call<JobListReponse> getJobList(@Url String url);

        @GET()
        Call<IndustryResponse> getIndustry(@Url String url);

        @GET
        Call<JobLocationResponse> getJobLocation(@Url String url);

        @GET
        Call<SkillResponse> getSkill(@Url String url);

        @Multipart
        @POST("users/postAvatarUploadFile")
        Call<UploadFileResponse> postuploadAvatar(@Part("sign") RequestBody sign,
                                                  @Part("app_id") RequestBody app_id,
                                                  @Part("device_type") RequestBody device_type,
                                                  @Part("ApiToken") RequestBody ApiToken,
                                                  @Part("UserID") RequestBody userid,
                                                  @Part("Files\"; filename=\"avatar.jpg\"") RequestBody file);

        @GET("users/getUserDetail/{sign}/{app_id}/{device_type}/{user_id}/{token}")
        Call<UserDetailResponse> getUserDetail(@Path("sign") String sign,
                                               @Path("app_id") String app_id,
                                               @Path("device_type") int device_type,
                                               @Path("user_id") int user_id,
                                               @Path("token") String token,
                                               @Query("user_id") int user_id1);

        @POST("users/postUpdateJobSeeker")
        Call<BaseResponse> postUpdateDetail(@Body UpdateProfileRequest updateProfileRequest);

        @GET("jobseekerexperience/getAllJobSeekerExperience/{sign}/{app_id}/{device_type}/{user_id}/{token}")
        Call<ExperienceResponse> getExperience(@Path("sign") String sign,
                                               @Path("app_id") String app_id,
                                               @Path("device_type") int device_type,
                                               @Path("user_id") int user_id,
                                               @Path("token") String token,
                                               @Query("user_id") int user_id1);

        @POST("jobseekerexperience/postAddJobSeekerExperience")
        Call<BaseResponse> postAddJobSeekerExperience(@Body ExperienceRequest addExperienceRequest);

        @POST("jobseekerexperience/postUpdateJobSeekerExperience")
        Call<BaseResponse> postUpdateJobSeekerExperience(@Body ExperienceRequest addExperienceRequest);

        @GET("skill/getListSkill/{sign}/{app_id}/{device_type}/{user_id}")
        Call<SkillResponse> getSkill(@Path("sign") String sign,
                                     @Path("app_id") String app_id,
                                     @Path("device_type") int device_type,
                                     @Path("user_id") int user_id);

        @POST("skill/postEditSkill")
        Call<BaseResponse> postEditSkill(@Body SkillRequest skillRequest);

        @GET("jobs/getJobDetail/{sign}/{app_id}/{device_type}/{user_id}/{job_id}")
        Call<DetailJobResponse> getDetailJob(@Path("sign") String sign,
                                             @Path("app_id") String app_id,
                                             @Path("device_type") int device_type,
                                             @Path("user_id") int user_id,
                                             @Path("job_id") int job_id);

        @GET("users/getLogout/{sign}/{app_id}/{device_type}/{user_id}/{ApiToken}")
        Call<BaseResponse> getLogout(@Path("sign") String sign,
                                          @Path("app_id") String app_id,
                                          @Path("device_type") int device_type,
                                          @Path("user_id") int user_id,
                                          @Path("ApiToken") String token);
    }
}
