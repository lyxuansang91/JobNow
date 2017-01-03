package com.newtech.jobnow.acitvity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.adapter.JobListAdapter;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.common.FunctionCommon;
import com.newtech.jobnow.models.TokenRequest;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.eventbus.ApplyJobEvent;
import com.newtech.jobnow.eventbus.SaveJobEvent;
import com.newtech.jobnow.models.ApplyJobRequest;
import com.newtech.jobnow.models.BaseResponse;
import com.newtech.jobnow.models.DeleteJobRequest;
import com.newtech.jobnow.models.DetailJobResponse;
import com.newtech.jobnow.models.JobObject;
import com.newtech.jobnow.models.LoginResponse;
import com.newtech.jobnow.models.SaveJobRequest;
import com.newtech.jobnow.utils.Utils;
import com.ocpsoft.pretty.time.PrettyTime;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailJobsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = DetailJobsActivity.class.getSimpleName();
    public static final String KEY_LAT = "lattitude";
    public static final String KEY_LNG = "longtitude";
    private static final int DELAY_TIME = 3000;

    private Handler mHandler = new Handler();

    private TextView tvName, tvLocation, tvPrice, tvTime, tvCompanyName, tvDescription,
            tvRequirement, tvYearOfExperience, tvPosition, tvCountUserApplyJob;
    private ImageView imgLogo, ivSaveJob;
    private LinearLayout lnSaveJob, lnApplyJob;
    private ProgressDialog progressDialog;
    private int jobId;
    private JobObject jobObject;
    private PrettyTime p;
    private boolean savedJob = false;
    private boolean appliedJob = false;
    private Button btnSaveJob, btnApplyJob;
    private ImageButton btnShareFacebook, btnShareTwitter, btnShareGooglePlus, btnShareLinkedIn,
    btnSharePinterest, btnShareRSS;
    private TextView tvViewLargerMap;
    private String shareUrl = null;
    private Double latitude, longtitude;


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
                    bindData1();
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
                    appliedJob = response.body().result.isApplyJob;
                    savedJob = response.body().result.isSaveJob;
                    ivSaveJob.setImageResource(savedJob ? R.mipmap.ic_saved_job : R.mipmap.ic_unsaved_job);
                    btnSaveJob.setText(savedJob ? "Unsaved" : "Save job");
                    btnApplyJob.setText(appliedJob ? "Unapplied" : "Apply job");
                    shareUrl = response.body().result.ShareUrl;
                    latitude = response.body().result.latitude;
                    longtitude = response.body().result.longtitude;
                    tvYearOfExperience.setText(jobObject.YearOfExperience);
                    tvCountUserApplyJob.setText(jobObject.CountUserApplyJob + " Applications");
                } else if(response.body().code == 503) {
                    getApiToken();
                    bindData1();
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
        tvViewLargerMap = (TextView) findViewById(R.id.tvViewLargerMap);
        //apply job and save job
        lnSaveJob = (LinearLayout) findViewById(R.id.lnSaveJob);
        lnApplyJob = (LinearLayout) findViewById(R.id.lnApplyJob);
        btnSaveJob = (Button) findViewById(R.id.btnSaveJob);
        btnApplyJob = (Button) findViewById(R.id.btnApplyJob);
        ivSaveJob = (ImageView) findViewById(R.id.ivSaveJob);
        lnSaveJob.setOnClickListener(this);
        lnApplyJob.setOnClickListener(this);
        btnSaveJob.setOnClickListener(this);
        btnApplyJob.setOnClickListener(this);
        btnShareFacebook = (ImageButton) findViewById(R.id.btnShareFacebook);
        btnShareTwitter = (ImageButton) findViewById(R.id.btnShareTwitter);
        btnShareGooglePlus = (ImageButton) findViewById(R.id.btnShareGoogle);
        btnShareLinkedIn = (ImageButton) findViewById(R.id.btnShareLinkedIn);
        btnSharePinterest = (ImageButton) findViewById(R.id.btnSharePinterest);
        btnShareRSS = (ImageButton) findViewById(R.id.btnShareRSS);
        btnShareFacebook.setOnClickListener(this);
        btnShareTwitter.setOnClickListener(this);
        btnShareGooglePlus.setOnClickListener(this);
        btnShareLinkedIn.setOnClickListener(this);
        btnSharePinterest.setOnClickListener(this);
        btnShareRSS.setOnClickListener(this);
        tvViewLargerMap.setOnClickListener(this);
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

    private void handleSaveJob() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<BaseResponse> call = service.saveJob(new SaveJobRequest(jobId, userId, userId, token));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                if (response.body() != null) {
                    BaseResponse baseResponse = response.body();

                    if (baseResponse.code == 200) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailJobsActivity.this);
                        LayoutInflater inflater = DetailJobsActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_success, null);
                        builder.setView(dialogView);
                        final AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        dialog.setCancelable(true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(dialog.isShowing())
                                    dialog.dismiss();
                            }
                        }, 3000);
                        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
                        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
                        tvTitle.setText("SAVED!");
                        tvMessage.setText(jobObject.Title + " " + response.body().message);

                        ivSaveJob.setImageResource(R.mipmap.ic_saved_job);
                        btnSaveJob.setText("Unsaved");
                        savedJob = true;
                        EventBus.getDefault().post(new SaveJobEvent());
                    } else {
                        Toast.makeText(getApplicationContext(), baseResponse.message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_connect),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUnsavedJob() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<BaseResponse> call = service.deleteSaveJob(new DeleteJobRequest(userId, jobId, token, userId,
                JobListAdapter.SAVE_TYPE));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                if (response.body() != null) {
                    BaseResponse baseResponse = response.body();
                    if (baseResponse.code == 200) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailJobsActivity.this);
                        LayoutInflater inflater = DetailJobsActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_success, null);
                        builder.setView(dialogView);
                        final AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        dialog.setCancelable(true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(dialog.isShowing())
                                    dialog.dismiss();
                            }
                        }, DELAY_TIME);
                        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
                        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
                        tvTitle.setText("UNSAVED!");
                        tvMessage.setText(jobObject.Title + " " + response.body().message);


                        ivSaveJob.setImageResource(R.mipmap.ic_unsaved_job);
                        btnSaveJob.setText("Save Job");
                        savedJob = false;
                        EventBus.getDefault().post(new SaveJobEvent());
                    } else {
                        Toast.makeText(getApplicationContext(), baseResponse.message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void handleUnappliedJob() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<BaseResponse> call = service.deleteAppliedJob(new DeleteJobRequest(userId, jobId, token, userId,
                JobListAdapter.APPLY_TYPE));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                if (response.body() != null) {
                    BaseResponse baseResponse = response.body();

                    if (baseResponse.code == 200) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailJobsActivity.this);
                        LayoutInflater inflater = DetailJobsActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_success, null);
                        builder.setView(dialogView);
                        final AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        dialog.setCancelable(true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(dialog.isShowing())
                                    dialog.dismiss();
                            }
                        }, DELAY_TIME);
                        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
                        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
                        tvTitle.setText("Unapplied!".toUpperCase());
                        tvMessage.setText(jobObject.Title + " " + response.body().message);

                        btnApplyJob.setText("Apply Job");
                        appliedJob = false;
                        EventBus.getDefault().post(new ApplyJobEvent());
                    } else {
                        Toast.makeText(getApplicationContext(), baseResponse.message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

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
                if (response.body() != null) {
                    BaseResponse baseResponse = response.body();
                    if (baseResponse.code == 200) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailJobsActivity.this);
                        LayoutInflater inflater = DetailJobsActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_success, null);
                        builder.setView(dialogView);
                        final AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        dialog.setCancelable(true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(dialog.isShowing())
                                    dialog.dismiss();
                            }
                        }, DELAY_TIME);
                        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
                        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
                        tvTitle.setText("Applied!".toUpperCase());
                        tvMessage.setText(jobObject.Title + " " + response.body().message);


                        btnApplyJob.setText("Applied");
                        appliedJob = true;
                        EventBus.getDefault().post(new ApplyJobEvent());
                    } else {
                        Toast.makeText(getApplicationContext(), baseResponse.message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_connect),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void shareTwitter() {
        String tweetUrl = String.format("https://twitter.com/intent/tweet?url=%s",
                FunctionCommon.urlEncode(shareUrl));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
                break;
            }
        }
        startActivity(intent);
    }

    private void shareFacebook() {

        String facebookUrl = String.format("http://www.facebook.com/sharer/sharer.php?u=%s",
                FunctionCommon.urlEncode(shareUrl));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                break;
            }
        }
        startActivity(intent);
    }

    private void sharePinterest() {
        String url = String.format(
                "https://www.pinterest.com/pin/create/button/?url=%s",
                FunctionCommon.urlEncode(shareUrl));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.pinterest")) {
                intent.setPackage(info.activityInfo.packageName);
                break;
            }
        }
        startActivity(intent);
    }

    private void shareGooglePlus() {
        String url = String.format(
                "https://plus.google.com/share?url=%s",
                FunctionCommon.urlEncode(shareUrl));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.google.android.apps.plus")) {
                intent.setPackage(info.activityInfo.packageName);
                break;
            }
        }
        startActivity(intent);
    }

    private void shareLinkedIn() {
        String linkedInUrl = String.format("https://www.linkedin.com/cws/share?url=%s",
                FunctionCommon.urlEncode(shareUrl));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedInUrl));
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.linkedin.android")) {
                intent.setPackage(info.activityInfo.packageName);
                break;
            }
        }
        startActivity(intent);

    }

    private void shareSNS() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void goToMap() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MapsActivity.KEY_JOB_ID, jobId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lnSaveJob:
            case R.id.btnSaveJob:
                if (savedJob) {
                    handleUnsavedJob();
                } else
                    handleSaveJob();
                break;
            case R.id.lnApplyJob:
            case R.id.btnApplyJob:
                if (appliedJob) {
                    handleUnappliedJob();
                } else
                    handleApplyJob();
                break;
            case R.id.btnShareFacebook:
                shareFacebook();
                break;
            case R.id.btnShareTwitter:
                shareTwitter();
                break;
            case R.id.btnShareGoogle:
                shareGooglePlus();
                break;
            case R.id.btnShareLinkedIn:
                shareLinkedIn();
                break;
            case R.id.btnSharePinterest:
                sharePinterest();
                break;
            case R.id.btnShareRSS:
                shareSNS();
                break;
            case R.id.tvViewLargerMap:
                goToMap();
                break;
        }
    }
}
