package com.androidteam.jobnow.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.MyApplication;
import com.androidteam.jobnow.adapter.JobListAdapter;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.models.JobListReponse;
import com.androidteam.jobnow.models.JobObject;
import com.androidteam.jobnow.utils.Utils;
import com.androidteam.jobnow.widget.CRecyclerView;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppliedJobListFragment extends Fragment {
    public static final String TAG = AppliedJobListFragment.class.getSimpleName();

    public AppliedJobListFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance() {
        return new AppliedJobListFragment();
    }

    //    private LinearLayout lnJob1, lnJob2, lnJob3;
    private RelativeLayout rlSearchBar;
    private CRecyclerView rvListJob;
    private JobListAdapter adapter;
    private TextView tvNumberJob;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_applied_job_list, container, false);
        initUI(rootView);
        bindData();
        return rootView;
    }

    private void bindData() {
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        Call<JobListReponse> request = service.getAppliedListJob(
                APICommon.getSign(APICommon.getApiKey(), "/api/v1/jobs/getAppliedJob"),
                APICommon.getAppId(),
                APICommon.getDeviceType(),
                userId,
                token, 0);
        request.enqueue(new Callback<JobListReponse>() {
            @Override
            public void onResponse(Response<JobListReponse> response, Retrofit retrofit) {
                JobListReponse jobList = response.body();
                if(jobList != null) {
                    if(jobList.code == 200) {

                        JobListReponse.JobListResult result = jobList.result;
                        if(result != null) {
                            tvNumberJob.setText(result.total + " applied job");
                            adapter.addAll(result.data);
                        }
                    } else {
                        Toast.makeText(getActivity(), jobList.message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.error_connect),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI(View view) {
        rlSearchBar = (RelativeLayout) view.findViewById(R.id.rlSearchBar);
        rvListJob = (CRecyclerView) view.findViewById(R.id.rvListJob);
        rvListJob.setDivider();
        adapter = new JobListAdapter(getActivity(), new ArrayList<JobObject>());
        rvListJob.setAdapter(adapter);
        tvNumberJob = (TextView) view.findViewById(R.id.tvNumberJob);
        Utils.closeKeyboard(getActivity());
    }

}
