package com.androidteam.jobnow.fragment;


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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.AddExperienceActivity;
import com.androidteam.jobnow.acitvity.EditExperienceActivity;
import com.androidteam.jobnow.acitvity.MyApplication;
import com.androidteam.jobnow.adapter.ExperienceAdapter;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.eventbus.EditExperienceEvent;
import com.androidteam.jobnow.models.ExperienceResponse;
import com.androidteam.jobnow.widget.CRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExperienceFragment extends Fragment implements View.OnClickListener {

    private CRecyclerView rvExperience;
    private ExperienceAdapter adapter;
    private ProgressDialog progressDialog;

    public ExperienceFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_experience, container, false);
        initUI(rootView);
        bindData();
        return rootView;
    }

    private void bindData() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<ExperienceResponse> call = service.getExperience(
                APICommon.getSign(APICommon.getApiKey(), "api/v1/jobseekerexperience/getAllJobSeekerExperience"),
                APICommon.getAppId(), APICommon.getDeviceType(), userId, token, userId);
        call.enqueue(new Callback<ExperienceResponse>() {
            @Override
            public void onResponse(Response<ExperienceResponse> response, Retrofit retrofit) {
                progressDialog.dismiss();
                if (response.body() != null && response.body().code == 200) {
                    adapter.clear();
                    adapter.addAll(response.body().result);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initUI(View rootView) {
        rvExperience = (CRecyclerView) rootView.findViewById(R.id.rvExperience);
        rvExperience.setDivider();
        adapter = new ExperienceAdapter(getActivity(), new ArrayList<ExperienceResponse.Experience>());
        rvExperience.setAdapter(adapter);
        LinearLayout lnAddExp = (LinearLayout) rootView.findViewById(R.id.lnAddExp);
        lnAddExp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.lnAddExp:
                intent = new Intent(getActivity(), AddExperienceActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                bindData();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDetach() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDetach();
    }

    @Subscribe
    public void onEvent(EditExperienceEvent event) {
        Intent intent = new Intent(getActivity(), EditExperienceActivity.class);
        intent.putExtra("experience", event.experience);
        startActivityForResult(intent, 1);
    }
}
