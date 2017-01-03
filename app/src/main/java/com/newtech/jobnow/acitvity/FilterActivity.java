package com.newtech.jobnow.acitvity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.adapter.IndustryAdapter;
import com.newtech.jobnow.adapter.JobLocationAdapter;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.models.IndustryObject;
import com.newtech.jobnow.models.IndustryResponse;
import com.newtech.jobnow.models.JobListRequest;
import com.newtech.jobnow.models.JobLocationResponse;
import com.newtech.jobnow.utils.Utils;
import com.newtech.jobnow.widget.DisableScrollRecyclerView;

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
    private DisableScrollRecyclerView rvJobLocation;
    //rvSkill;
    private JobLocationAdapter jobLocationAdapter;
//    private SkillAdapter skillAdapter;
    private Button btnFilter;
    private TextView tvReset;
    private EditText edtTitle, edtMinSalary;

    private void initUI() {

        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
        spnIndustry = (Spinner) findViewById(R.id.spnIndustry);
        rvJobLocation = (DisableScrollRecyclerView) findViewById(R.id.rvJobLocation);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtMinSalary = (EditText) findViewById(R.id.edtMinimumSalary);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvJobLocation.setLayoutManager(layoutManager);
        rvJobLocation.setHasFixedSize(true);
        rvJobLocation.setItemAnimator(new DefaultItemAnimator());
        jobLocationAdapter = new JobLocationAdapter(FilterActivity.this, new ArrayList<JobLocationResponse.JobLocation>());
        rvJobLocation.setAdapter(jobLocationAdapter);

//        rvSkill = (DisableScrollRecyclerView) findViewById(rvSkill);
//        LinearLayoutManager skillManager = new LinearLayoutManager(this);
//        rvSkill.setLayoutManager(skillManager);
//        rvSkill.setHasFixedSize(true);
//        rvSkill.setItemAnimator(new DefaultItemAnimator());
//        skillAdapter = new SkillAdapter(FilterActivity.this, new ArrayList<SkillResponse.Skill>());
//        rvSkill.setAdapter(skillAdapter);
        tvReset = (TextView) findViewById(R.id.tvReset);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initUI();
        bindData();
        initevent();

        Utils.closeKeyboard(FilterActivity.this);
    }

    private String getLocationParseFromLocations() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jobLocationAdapter.getItemCount(); i++) {
            JobLocationResponse.JobLocation location = jobLocationAdapter.getItembyPostion(i);
            if (location != null) {
                if (location.isChecked) {
                    if (sb.toString().equals("")) {
                        sb.append(location.id);
                    } else {
                        sb.append(',');
                        sb.append(location.id);
                    }
                }
            }
        }
        Log.d(TAG, "location : " + sb.toString());
        return sb.toString();
    }

//    private String getSkillParseFromSkills() {
//        StringBuilder sb = new StringBuilder("");
//        for (int i = 0; i < skillAdapter.getItemCount(); i++) {
//            SkillResponse.Skill skillItem = skillAdapter.getItembyPostion(i);
//            if (skillItem != null) {
//                if (skillItem.isSelected == 1) {
//                    if (sb.toString().equals("")) {
//                        sb.append(skillItem.id);
//                    } else {
//                        sb.append(',');
//                        sb.append(skillItem.id);
//                    }
//                }
//            }
//        }
//        Log.d(TAG, "get skill string: " + sb.toString());
//        return sb.toString();
//    }

    private Integer convertFromString(String number) {
        Integer result;
        try {
            result = Integer.parseInt(number);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;

        }
    }

    private void clearSelectData() {
        //skill
        spnIndustry.setSelection(0);
//        for(int i = 0; i < skillAdapter.getItemCount(); i++) {
//            SkillResponse.Skill skillItem = skillAdapter.getItembyPostion(i);
//            if(skillItem.isSelected == 1) {
//                skillItem.isSelected = 0;
//                skillAdapter.setData(i, skillItem);
//            }
//        }
        //location
        for(int i = 0; i < jobLocationAdapter.getItemCount(); i++) {
            JobLocationResponse.JobLocation jobLocation = jobLocationAdapter.getItembyPostion(i);
            if(jobLocation.isChecked) {
                jobLocation.isChecked = false;
                jobLocationAdapter.setData(i, jobLocation);
            }
        }
        edtMinSalary.setText("");
        edtTitle.setText("");
    }

    private void initevent() {
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                String title = edtTitle.getText().toString();
                if (title.equals(""))
                    title = null;

                Integer minSalary = convertFromString(edtMinSalary.getText().toString());

                IndustryObject industry = industryAdapter.getItem(
                        spnIndustry.getSelectedItemPosition());

                Integer industryId = industry.id;

//                String skill = getSkillParseFromSkills();
                String location = getLocationParseFromLocations();
                JobListRequest request = new JobListRequest(1, "ASC", title, location, "",
                        minSalary, null, null, industryId);

                Bundle bundle = new Bundle();
                bundle.putSerializable(SearchResultActivity.KEY_JOB, request);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelectData();
            }
        });
    }

    private void bindData() {
        getIndustry();
//        getSkill();
        getJobLocation();
    }

//    private void getSkill() {
//        final ProgressDialog progressDialog = ProgressDialog.show(FilterActivity.this,
//                "Loading", "Please wait..", true, false);
//        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
//        String url = APICommon.BASE_URL + "skill/getListSkill/" + APICommon.getSign(APICommon.getApiKey(), "api/v1/skill/getListSkill")
//                + "/" + APICommon.getAppId() + "/" + APICommon.getDeviceType() + "/0";
//        Call<SkillResponse> jobLocationResponseCall = service.getSkill(url);
//        jobLocationResponseCall.enqueue(new Callback<SkillResponse>() {
//            @Override
//            public void onResponse(Response<SkillResponse> response, Retrofit retrofit) {
//                progressDialog.dismiss();
//                if (response.body() != null && response.body().code == 200) {
//                    if (response.body().result != null && response.body().result.size() > 0) {
//                        skillAdapter.clear();
////                        skillAdapter.addAll(response.body().result);
//                        for (int i = 0; i < response.body().result.size(); i++) {
//                            if (response.body().result.get(i).isSelected == null) {
//                                response.body().result.get(i).isSelected = 0;
//                            }
//                        }
//                        skillAdapter.addAll(response.body().result);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                progressDialog.dismiss();
//                Toast.makeText(FilterActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void getJobLocation() {
        final ProgressDialog progressDialog = ProgressDialog.show(FilterActivity.this,
                "Loading", "Please wait..", true, false);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        String url = APICommon.BASE_URL + "country/getAllCountry/" + APICommon.getSign(APICommon.getApiKey(), "api/v1/country/getAllCountry")
                + "/" + APICommon.getAppId() + "/" + APICommon.getDeviceType();
        Call<JobLocationResponse> jobLocationResponseCall = service.getJobLocation(url);
        jobLocationResponseCall.enqueue(new Callback<JobLocationResponse>() {
            @Override
            public void onResponse(Response<JobLocationResponse> response, Retrofit retrofit) {
                progressDialog.dismiss();
                if (response.body() != null && response.body().code == 200) {
                    if (response.body().result != null && response.body().result.size() > 0) {
                        jobLocationAdapter.addAll(response.body().result);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(FilterActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIndustry() {
        final ProgressDialog progressDialog = ProgressDialog.show(FilterActivity.this,
                "Loading", "Please wait..", true, false);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        String url = APICommon.BASE_URL + "industry/getListIndustry/" + APICommon.getSign(APICommon.getApiKey(), "api/v1/industry/getListIndustry")
                + "/" + APICommon.getAppId() + "/" + APICommon.getDeviceType();
        Call<IndustryResponse> industryResponseCall = service.getIndustry(url);
        industryResponseCall.enqueue(new Callback<IndustryResponse>() {
            @Override
            public void onResponse(Response<IndustryResponse> response, Retrofit retrofit) {
                progressDialog.dismiss();
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
                progressDialog.dismiss();
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
