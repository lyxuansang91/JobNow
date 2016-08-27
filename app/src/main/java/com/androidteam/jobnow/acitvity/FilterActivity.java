package com.androidteam.jobnow.acitvity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.adapter.IndustryAdapter;
import com.androidteam.jobnow.adapter.JobLocationAdapter;
import com.androidteam.jobnow.adapter.SkillAdapter;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.models.IndustryObject;
import com.androidteam.jobnow.models.IndustryResponse;
import com.androidteam.jobnow.models.JobLocationResponse;
import com.androidteam.jobnow.models.SkillResponse;
import com.androidteam.jobnow.widget.DisableScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FilterActivity extends AppCompatActivity {
    private String TAG = FilterActivity.class.getSimpleName();
    private IndustryAdapter industryAdapter;
    private Spinner spnIndustry;
    private DisableScrollRecyclerView rvJobLocation, rvSkill;
    private JobLocationAdapter jobLocationAdapter;
    private SkillAdapter skillAdapter;
    private RelativeLayout imgBack;
    private void initUI() {
        imgBack = (RelativeLayout) findViewById(R.id.imgBack);
        spnIndustry = (Spinner) findViewById(R.id.spnIndustry);
        rvJobLocation = (DisableScrollRecyclerView) findViewById(R.id.rvJobLocation);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvJobLocation.setLayoutManager(layoutManager);
        rvJobLocation.setHasFixedSize(true);
        rvJobLocation.setItemAnimator(new DefaultItemAnimator());
        jobLocationAdapter = new JobLocationAdapter(FilterActivity.this, new ArrayList<JobLocationResponse.JobLocationResult>());
        rvJobLocation.setAdapter(jobLocationAdapter);

        rvSkill = (DisableScrollRecyclerView) findViewById(R.id.rvSkill);
        LinearLayoutManager skillManager = new LinearLayoutManager(this);
        rvSkill.setLayoutManager(skillManager);
        rvSkill.setHasFixedSize(true);
        rvSkill.setItemAnimator(new DefaultItemAnimator());
        skillAdapter = new SkillAdapter(FilterActivity.this, new ArrayList<SkillResponse.Skill>());
        rvSkill.setAdapter(skillAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initUI();
        bindData();
        initevent();
    }

    private void initevent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void bindData() {
        getIndustry();
        getJobLocation();
        getSkill();
    }

    private void getSkill() {
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        String url = APICommon.BASE_URL + "skill/getListSkill/" + APICommon.getSign(APICommon.getApiKey(), "api/v1/skill/getListSkill")
                + "/" + APICommon.getAppId() + "/" + APICommon.getDeviceType();
        Call<SkillResponse> jobLocationResponseCall = service.getSkill(url);
        jobLocationResponseCall.enqueue(new Callback<SkillResponse>() {
            @Override
            public void onResponse(Response<SkillResponse> response, Retrofit retrofit) {
                if (response.body() != null && response.body().code == 200) {
                    if (response.body().result != null && response.body().result.size() > 0) {
                        skillAdapter.addAll(response.body().result);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(FilterActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getJobLocation() {
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        String url = APICommon.BASE_URL + "country/getAllCountry/" + APICommon.getSign(APICommon.getApiKey(), "api/v1/country/getAllCountry")
                + "/" + APICommon.getAppId() + "/" + APICommon.getDeviceType();
        Call<JobLocationResponse> jobLocationResponseCall = service.getJobLocation(url);
        jobLocationResponseCall.enqueue(new Callback<JobLocationResponse>() {
            @Override
            public void onResponse(Response<JobLocationResponse> response, Retrofit retrofit) {
                if (response.body() != null && response.body().code == 200) {
                    if (response.body().result != null && response.body().result.size() > 0) {
                        jobLocationAdapter.addAll(response.body().result);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(FilterActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });

//        for (int i = 0; i < 9; i++) {
//            jobLocationAdapter.add(new JobLocationResponse.JobLocationResult());
//        }
    }

    private void getIndustry() {
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        String url = APICommon.BASE_URL + "industry/getListIndustry/" + APICommon.getSign(APICommon.getApiKey(), "api/v1/industry/getListIndustry")
                + "/" + APICommon.getAppId() + "/" + APICommon.getDeviceType();
        Call<IndustryResponse> industryResponseCall = service.getIndustry(url);
        industryResponseCall.enqueue(new Callback<IndustryResponse>() {
            @Override
            public void onResponse(Response<IndustryResponse> response, Retrofit retrofit) {
                if (response.body() != null && response.body().code == 200) {
                    List<IndustryObject> industryObjects = new ArrayList<>();
                    IndustryObject industryObject = new IndustryObject();
                    industryObject.id = 0;
                    industryObject.Name = getString(R.string.AllSpecializations);
                    industryObjects.add(industryObject);
                    if (response.body().result != null && response.body().result.size() > 0)
                        industryObjects.addAll(response.body().result);
                    industryAdapter = new IndustryAdapter(FilterActivity.this, industryObjects);
                    spnIndustry.setAdapter(industryAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(FilterActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
