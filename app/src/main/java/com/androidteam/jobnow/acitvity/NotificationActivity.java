package com.androidteam.jobnow.acitvity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.adapter.NotificationAdapter;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.models.NotificationObject;
import com.androidteam.jobnow.models.NotificationResponse;
import com.androidteam.jobnow.widget.CRecyclerView;

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
