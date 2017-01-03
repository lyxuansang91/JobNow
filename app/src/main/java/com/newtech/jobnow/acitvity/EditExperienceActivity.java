package com.newtech.jobnow.acitvity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.models.ExperienceRequest;
import com.newtech.jobnow.models.BaseResponse;
import com.newtech.jobnow.models.ExperienceResponse;
import com.newtech.jobnow.models.LoginResponse;
import com.newtech.jobnow.models.TokenRequest;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EditExperienceActivity extends AppCompatActivity implements View.OnClickListener {

    private ExperienceResponse.Experience experience;
    private Button btnSave, btnCancel, btnRemove;
    private EditText edCompanyName, edJobOrPosition, edDescription;
    private String companyName, location, description;
    private String TAG = EditExperienceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_experience);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Experience");

        Intent intent = getIntent();
        experience = (ExperienceResponse.Experience) intent.getSerializableExtra("experience");
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

    private void event() {
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
    }

    private void bindData() {
        edCompanyName.setText(experience.CompanyName);
        edJobOrPosition.setText(experience.PositionName);
        edDescription.setText(experience.Description);
    }

    private void initUI() {
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnRemove = (Button) findViewById(R.id.btnRemove);
        edCompanyName = (EditText) findViewById(R.id.edCompanyName);
        edJobOrPosition = (EditText) findViewById(R.id.edJobOrPosition);
        edDescription = (EditText) findViewById(R.id.edDescription);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                companyName = edCompanyName.getText().toString();
                location = edJobOrPosition.getText().toString();
                description = edDescription.getText().toString();
                if (companyName == null || companyName.isEmpty()) {
                    Toast.makeText(this, getString(R.string.pleaseInputCompanyName), Toast.LENGTH_SHORT).show();
                } else if (location == null || location.isEmpty()) {
                    Toast.makeText(this, getString(R.string.pleaseInputJobOrPosition), Toast.LENGTH_SHORT).show();
                } else if (description == null || description.isEmpty()) {
                    Toast.makeText(this, getString(R.string.pleaseInputDescription), Toast.LENGTH_SHORT).show();
                } else {
                    save();
                }
                break;
            case R.id.btnRemove:
                companyName = edCompanyName.getText().toString();
                location = edJobOrPosition.getText().toString();
                description = edDescription.getText().toString();
                delete();
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    }

    private void delete() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<BaseResponse> call = service.postDeleteJobSeekerExperience(new ExperienceRequest(experience.id, userId, token, userId));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                if (response.body() != null) {
                    if (response.body().code == 200) {
                        setResult(RESULT_OK);
                        finish();
                    } else if(response.body().code == 503) {
                        getApiToken();
                        Toast.makeText(EditExperienceActivity.this, response.body().message,
                                Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(EditExperienceActivity.this, response.body().message,
                                Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(EditExperienceActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void save() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<BaseResponse> call = service.postUpdateJobSeekerExperience(new ExperienceRequest(userId, companyName, location, description, token, userId, experience.id, ExperienceRequest.UPDATE));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                Toast.makeText(EditExperienceActivity.this, response.body().message,
                        Toast.LENGTH_SHORT).show();
                if (response.body() != null) {
                    if (response.body().code == 200) {
                        setResult(RESULT_OK);
                        finish();
                    } else if(response.body().code == 503) {
                        getApiToken();
                        Toast.makeText(EditExperienceActivity.this, response.body().message,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditExperienceActivity.this, response.body().message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(EditExperienceActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
