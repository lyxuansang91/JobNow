package com.newtech.jobnow.acitvity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.adapter.NotificationAdapter;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.models.LoginResponse;
import com.newtech.jobnow.models.NotificationObject;
import com.newtech.jobnow.models.NotificationResponse;
import com.newtech.jobnow.models.TokenRequest;
import com.newtech.jobnow.widget.CRecyclerView;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NotificationActivity extends AppCompatActivity {

    public static final String TAG = NotificationActivity.class.getSimpleName();
    private CRecyclerView rvNotification;
    private NotificationAdapter adapter;
    private RelativeLayout btnBack, btnRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initUI();
        bindData();
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
                    bindData();
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

    private void bindData() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Loading", "Please wait...", true, false);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);

        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<NotificationResponse> call = service.getListNotification(
                APICommon.getSign(APICommon.getApiKey(), "api/v1/notification/getListNotification"),
                APICommon.getAppId(),
                APICommon.getDeviceType(),
                userId,
                token);
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Response<NotificationResponse> response, Retrofit retrofit) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    NotificationResponse notificationResponse = response.body();
                    if (notificationResponse.code == 200) {
                        if (notificationResponse.result.size() > 0)
                            adapter.addAll(notificationResponse.result);
                    } else if(notificationResponse.code == 503){
                        getApiToken();
                        Toast.makeText(getApplicationContext(), notificationResponse.message,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), notificationResponse.message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.error_connect, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void initUI() {
        rvNotification = (CRecyclerView) findViewById(R.id.rvNotification);
        rvNotification.setDivider();
        btnBack = (RelativeLayout) findViewById(R.id.btnBack);
        btnRemove = (RelativeLayout) findViewById(R.id.btnRemove);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rvNotification.setHasFixedSize(true);
        rvNotification.setItemAnimator(new DefaultItemAnimator());

        adapter = new NotificationAdapter(NotificationActivity.this,
                new ArrayList<NotificationObject>());
        rvNotification.setAdapter(adapter);
    }
}
