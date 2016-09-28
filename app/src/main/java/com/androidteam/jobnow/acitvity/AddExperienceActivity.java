package com.androidteam.jobnow.acitvity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.models.ExperienceRequest;
import com.androidteam.jobnow.models.BaseResponse;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class AddExperienceActivity extends AppCompatActivity {
    private EditText edCompanyName, edJobOrPosition, edDescription;
    private Button btnCancel, btnSave;
    public String companyName, location, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_experience);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Experience");
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);

        edCompanyName = (EditText) findViewById(R.id.edCompanyName);
        edJobOrPosition = (EditText) findViewById(R.id.edJobOrPosition);
        edDescription = (EditText) findViewById(R.id.edDescription);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companyName = edCompanyName.getText().toString();
                location = edJobOrPosition.getText().toString();
                description = edDescription.getText().toString();
                if (companyName == null || companyName.isEmpty()) {
                    Toast.makeText(AddExperienceActivity.this, getString(R.string.pleaseInputCompanyName), Toast.LENGTH_SHORT).show();
                } else if (location == null || location.isEmpty()) {
                    Toast.makeText(AddExperienceActivity.this, getString(R.string.pleaseInputJobOrPosition), Toast.LENGTH_SHORT).show();
                } else if (description == null || description.isEmpty()) {
                    Toast.makeText(AddExperienceActivity.this, getString(R.string.pleaseInputDescription), Toast.LENGTH_SHORT).show();
                } else {
                    addExepience();
                }

            }
        });
    }

    private void addExepience() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<BaseResponse> call = service.postAddJobSeekerExperience(new ExperienceRequest(userId, companyName, location, description, token, userId));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                if (response.body() != null) {
                    Toast.makeText(AddExperienceActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    if (response.body().code == 200) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(AddExperienceActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
