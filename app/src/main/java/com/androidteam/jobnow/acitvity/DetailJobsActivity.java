package com.androidteam.jobnow.acitvity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.models.ApplyJobRequest;
import com.androidteam.jobnow.models.BaseResponse;
import com.androidteam.jobnow.models.DetailJobResponse;
import com.androidteam.jobnow.models.JobObject;
import com.androidteam.jobnow.models.SaveJobRequest;
import com.androidteam.jobnow.utils.Utils;
import com.ocpsoft.pretty.time.PrettyTime;
import com.squareup.picasso.Picasso;

import java.util.Date;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailJobsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvName, tvLocation, tvPrice, tvTime, tvCompanyName, tvDescription,
            tvRequirement, tvYearOfExperience, tvPosition,tvCountUserApplyJob;
    private ImageView imgLogo;
    private LinearLayout lnSaveJob, lnApplyJob;
    private ProgressDialog progressDialog;
    private int jobId;
    private JobObject jobObject;
    private PrettyTime p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jobs);
        Intent intent = getIntent();
        jobId = intent.getIntExtra("jobId", 0);
//        jobObject = (JobObject) intent.getSerializableExtra("jobObject");
        initUI();
        p = new PrettyTime();
        bindData1();
    }

    private void bindData() {

        if (jobObject != null) {
            tvName.setText(jobObject.Title);
            tvPosition.setText(jobObject.Position);
            tvLocation.setText(getString(R.string.address) + " " + jobObject.LocationName);
            tvPrice.setText(jobObject.FromSalary + " - " + jobObject.ToSalary + " (USD)");
            tvTime.setText(getString(R.string.posted) + " " + p.format(new Date(Utils.getLongTime(jobObject.created_at))));
            tvCompanyName.setText(jobObject.CompanyName);
            Picasso.with(this).load(jobObject.CompanyLogo).placeholder(R.mipmap.img_logo_company).error(R.mipmap.img_logo_company).into(imgLogo);
            tvDescription.setText(jobObject.Description);
            tvRequirement.setText(jobObject.Requirement);
            tvYearOfExperience.setText(jobObject.YearOfExperience);
        }
    }


    private void bindData1() {
        progressDialog = ProgressDialog.show(this, "", getString(R.string.loading), true, true);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<DetailJobResponse> call = service.getDetailJob(
                APICommon.getSign(APICommon.getApiKey(), "api/v1/jobs/getJobDetail"), APICommon.getAppId(), APICommon.getDeviceType(), userId, jobId);
        call.enqueue(new Callback<DetailJobResponse>() {
            @Override
            public void onResponse(Response<DetailJobResponse> response, Retrofit retrofit) {
                progressDialog.dismiss();
                if (response.body() != null && response.body().code == 200) {
                    jobObject = response.body().result;
                    tvName.setText(jobObject.Title);
                    tvPosition.setText(jobObject.Position);
                    tvLocation.setText(getString(R.string.address) + " " + jobObject.LocationName);
                    tvPrice.setText(jobObject.FromSalary + " - " + jobObject.ToSalary + " (USD)");
                    tvTime.setText(getString(R.string.posted) + " " + p.format(new Date(Utils.getLongTime(jobObject.created_at))));
                    tvCompanyName.setText(jobObject.CompanyName);
                    Picasso.with(DetailJobsActivity.this).load(jobObject.CompanyLogo).placeholder(R.mipmap.img_logo_company).error(R.mipmap.img_logo_company).into(imgLogo);
                    tvDescription.setText(jobObject.Description);
                    tvRequirement.setText(jobObject.Requirement);
                    tvYearOfExperience.setText(jobObject.YearOfExperience);
                    tvCountUserApplyJob.setText(jobObject.CountUserApplyJob+" Applications");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailJobsActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Job Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        tvName = (TextView) findViewById(R.id.tvName);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvPosition = (TextView) findViewById(R.id.tvPosition);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvCompanyName = (TextView) findViewById(R.id.tvCompanyName);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvRequirement = (TextView) findViewById(R.id.tvRequirement);
        tvYearOfExperience = (TextView) findViewById(R.id.tvYearOfExperience);
        tvCountUserApplyJob = (TextView) findViewById(R.id.tvCountUserApplyJob);
        //apply job and save job
        lnSaveJob = (LinearLayout) findViewById(R.id.lnSaveJob);
        lnApplyJob = (LinearLayout) findViewById(R.id.lnApplyJob);
        lnSaveJob.setOnClickListener(this);
        lnApplyJob.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (android.R.id.home):
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleSaveJob(){

        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<BaseResponse> call = service.saveJob(new SaveJobRequest(jobId, userId, userId, token));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
               if(response.body() != null) {
                   BaseResponse baseResponse = response.body();
                   Toast.makeText(getApplicationContext(), baseResponse.message, Toast.LENGTH_SHORT)
                           .show();
               }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_connect),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void handleApplyJob() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<BaseResponse> call = service.applyJob(new ApplyJobRequest(jobId, userId, userId, token));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                if(response.body() != null) {
                    BaseResponse baseResponse = response.body();
                    Toast.makeText(getApplicationContext(), baseResponse.message, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_connect),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lnSaveJob:
                handleSaveJob();
                break;
            case R.id.lnApplyJob:
                handleApplyJob();
                break;
        }
    }
}
