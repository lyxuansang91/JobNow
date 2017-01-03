package com.newtech.jobnow.acitvity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.adapter.SkillAdapter;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.models.BaseResponse;
import com.newtech.jobnow.models.LoginResponse;
import com.newtech.jobnow.models.SkillRequest;
import com.newtech.jobnow.models.SkillResponse;
import com.newtech.jobnow.models.TokenRequest;
import com.newtech.jobnow.widget.CRecyclerView;
import com.newtech.jobnow.widget.CenteredToolbar;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EditSkillActivity extends AppCompatActivity {
    private static final String TAG = EditSkillActivity.class.getSimpleName();
    private Button btnSave, btnCancel;
    private ProgressDialog progressDialog;
    private SkillAdapter adapter;
    private CRecyclerView rvSkill;
    private int[] skillCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_skill);
        initUI();
        bindData();
        event();

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
        progressDialog = ProgressDialog.show(this, "", getString(R.string.loading), true, true);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<SkillResponse> call = service.getSkill(
                APICommon.getSign(APICommon.getApiKey(), "api/v1/skill/getListSkill"),
                APICommon.getAppId(), APICommon.getDeviceType(), userId);
        call.enqueue(new Callback<SkillResponse>() {
            @Override
            public void onResponse(Response<SkillResponse> response, Retrofit retrofit) {
                progressDialog.dismiss();
                if (response.body() != null && response.body().code == 200) {
                    adapter.clear();
//                    adapter.addAll(response.body().result);
                    if (response.body().result != null && response.body().result.size() > 0) {
                        for (int i = 0; i < response.body().result.size(); i++) {
                            if (response.body().result.get(i).isSelected == null) {
                                response.body().result.get(i).isSelected = 0;
                            }
                            adapter.add(response.body().result.get(i));
                        }
                    }
                } else if(response.body().code == 503) {
                    getApiToken();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditSkillActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void event() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void save() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<BaseResponse> call = service.postEditSkill(new SkillRequest(token, userId, getSkillCheck()));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                if (response.body() != null) {
                    Toast.makeText(EditSkillActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    if (response.body().code == 200) {
                        setResult(RESULT_OK);
                        finish();
                    } else if(response.body().code == 503) {
                        MyApplication.getInstance().getApiToken();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(EditSkillActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
        setResult(RESULT_OK);
        finish();
    }

    public int[] getSkillCheck() {
        int count = 0;
        int[] skills = new int[0];
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItembyPostion(i).isSelected != null && adapter.getItembyPostion(i).isSelected == 1) {
                count++;
            }
        }
        if (count > 0) {
            skills = new int[count];
            int k = 0;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getItembyPostion(i).isSelected != null && adapter.getItembyPostion(i).isSelected == 1) {
                    skills[k] = adapter.getItembyPostion(i).id;
                    k++;
                }
            }
        }
        return skills;
    }

    private void initUI() {
        CenteredToolbar toolbar = (CenteredToolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Skill");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        rvSkill = (CRecyclerView) findViewById(R.id.rvSkill);
        adapter = new SkillAdapter(this, new ArrayList<SkillResponse.Skill>());
        rvSkill.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
