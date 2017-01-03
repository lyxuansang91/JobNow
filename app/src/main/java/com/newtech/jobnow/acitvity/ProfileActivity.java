package com.newtech.jobnow.acitvity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.eventbus.SaveJobListEvent;
import com.newtech.jobnow.fragment.AppliedJobListFragment;
import com.newtech.jobnow.fragment.MainFragment;
import com.newtech.jobnow.fragment.ProfileFragment;
import com.newtech.jobnow.fragment.SaveJobListFragment;
import com.newtech.jobnow.models.JobListReponse;
import com.newtech.jobnow.models.LoginResponse;
import com.newtech.jobnow.models.TokenRequest;
import com.newtech.jobnow.utils.Utils;
import com.newtech.jobnow.widget.TabEntity;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private CommonTabLayout tabbottom;
    private int[] mIconUnselectIds = {
            R.mipmap.ic_home_bottom, R.mipmap.ic_saved_bottom,
            R.mipmap.ic_applied_bottom, R.mipmap.ic_profile_bottom};
    private int[] mIconSelectIds = {
            R.mipmap.ic_home_bottom_selected, R.mipmap.ic_saved_bottom_selected,
            R.mipmap.ic_applied_bottom_selected, R.mipmap.ic_profile_bottom_selected};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments2 = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        InitUI();
        bindData();
        InitEvent();
        Utils.closeKeyboard(ProfileActivity.this);
    }

    private void bindData() {

        String[] mTitles = {getString(R.string.home), getString(R.string.saved), getString(R.string.applied), getString(R.string.profile)};
        for (int i = 0; i < mTitles.length; i++) {
            switch (i) {
                case 0:
                    mFragments2.add(new MainFragment());
                    break;
                case 1:
                    mFragments2.add(SaveJobListFragment.getInstance());
                    break;
                case 2:
                    mFragments2.add(AppliedJobListFragment.getInstance());
                    break;
                case 3:
                    mFragments2.add(new ProfileFragment());
                    break;
                default:
                    break;

            }
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        tabbottom.setTabData(mTabEntities, this, R.id.fl_change, mFragments2);
        //tabbottom.showMsg(1, 10);
        tabbottom.setCurrentTab(0);
    }

    private void InitEvent() {
    }

    private void InitUI() {
        tabbottom = (CommonTabLayout) findViewById(R.id.tabbottom);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Subscribe
    public void onEvent(SaveJobListEvent event) {
        Log.d(TAG, "total: " + event.total);
        int total = event.total;
        if (total != 0) {
            tabbottom.showMsg(1, total);
        } else
            tabbottom.hideMsg(1);
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
                    displayNumberOfSaveJob();
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

    private void displayNumberOfSaveJob() {

        final ProgressDialog progressDialog = ProgressDialog.show(ProfileActivity.this, "Loading...",
                "Please wait", true, false);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        SharedPreferences sharedPreferences = getSharedPreferences(
                Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        Call<JobListReponse> request = service.getSaveListJob(
                APICommon.getSign(APICommon.getApiKey(), "/api/v1/jobs/getSaveJob"),
                APICommon.getAppId(),
                APICommon.getDeviceType(),
                userId,
                token, null);
        request.enqueue(new Callback<JobListReponse>() {
            @Override
            public void onResponse(Response<JobListReponse> response, Retrofit retrofit) {
                progressDialog.dismiss();
                JobListReponse jobList = response.body();
                if(jobList != null) {
                    if(jobList.code == 200) {
                        JobListReponse.JobListResult result = jobList.result;
                        if(result != null) {
                            Log.d(TAG, "save job list total:" + result.total);
                            EventBus.getDefault().post(new SaveJobListEvent(result.total));
                        }
                    } else if(jobList.code == 503) {
                        getApiToken();
                        Toast.makeText(getApplicationContext(), jobList.message, Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.error_connect),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        displayNumberOfSaveJob();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - key_pressed < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), "Back again to exit", Toast.LENGTH_SHORT).show();
        }
        key_pressed = System.currentTimeMillis();
    }

    static long key_pressed;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
}
