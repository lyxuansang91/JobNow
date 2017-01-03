package com.newtech.jobnow.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.acitvity.EditSkillActivity;
import com.newtech.jobnow.acitvity.MyApplication;
import com.newtech.jobnow.adapter.MySkillAdapter;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.models.BaseResponse;
import com.newtech.jobnow.models.SkillRequest;
import com.newtech.jobnow.models.SkillResponse;
import com.newtech.jobnow.widget.CRecyclerView;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class SkillsFragment extends Fragment {
    private ImageView imgEdit;
    private CRecyclerView rvSkill;
    private MySkillAdapter adapter;
    private ProgressDialog progressDialog;
    private LinearLayout lnAddSkill;
    private LinearLayout lnRemoveSkill;

    public SkillsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_skills, container, false);
        initUI(view);
        bindData();
        event();

        return view;
    }


    private void bindData() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
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
                    if (response.body().result != null && response.body().result.size() > 0) {
                        for (int i = 0; i < response.body().result.size(); i++) {
                            if (response.body().result.get(i).isSelected != null && response.body().result.get(i).isSelected == 1) {
                                adapter.add(response.body().result.get(i));
                            }
                        }
                    }

                    if(response.body().result.size() == 0)
                        lnRemoveSkill.setVisibility(View.GONE);
                    else
                        lnRemoveSkill.setVisibility(View.VISIBLE);
                } else if(response.body().code == 503) {
                    MyApplication.getInstance().getApiToken(new MyApplication.TokenCallback() {
                        @Override
                        public void onTokenSuccess() {
                            bindData();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void removeAllSkill() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, true);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        int[] skills = new int[0];
        Call<BaseResponse> call = service.postEditSkill(new SkillRequest(token, userId, skills));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                    if (response.body().code == 200) {
                        bindData();
                    } else if(response.body().code == 503) {
                        MyApplication.getInstance().getApiToken(new MyApplication.TokenCallback() {
                            @Override
                            public void onTokenSuccess() {
                                removeAllSkill();
                            }
                        });

                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.error_connect), Toast.LENGTH_SHORT)
                        .show();

            }
        });
    }

    private void event() {
        lnAddSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditSkillActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        lnRemoveSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllSkill();
            }
        });
    }

    private void initUI(View view) {
        imgEdit = (ImageView) view.findViewById(R.id.imgEdit);
        lnAddSkill = (LinearLayout) view.findViewById(R.id.lnAddSkill);
        rvSkill = (CRecyclerView) view.findViewById(R.id.rvSkill);
        lnRemoveSkill = (LinearLayout) view.findViewById(R.id.lnRemoveSkill);
        adapter = new MySkillAdapter(getActivity(), new ArrayList<SkillResponse.Skill>());
        rvSkill.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                bindData();
            }
        }
    }
}
