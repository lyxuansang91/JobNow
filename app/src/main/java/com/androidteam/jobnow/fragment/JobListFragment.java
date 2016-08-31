package com.androidteam.jobnow.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.MyApplication;
import com.androidteam.jobnow.adapter.JobListAdapter;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.config.Config;
import com.androidteam.jobnow.models.JobListReponse;
import com.androidteam.jobnow.models.JobListRequest;
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
public class JobListFragment extends Fragment {
    private String TAG = JobListFragment.class.getSimpleName();
    public static String KEY_SEACH = "hasSearch";

    public JobListFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(boolean hasSearch) {
        Fragment fragment = new JobListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_SEACH, hasSearch);
        fragment.setArguments(bundle);
        return fragment;
    }

    //    private LinearLayout lnJob1, lnJob2, lnJob3;
    private RelativeLayout rlSearchBar;
    private CRecyclerView rvListJob;
    private JobListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_job_list, container, false);
        initUI(rootView);
        bindData();
        return rootView;
    }

    private void bindData() {
        JobListRequest jobListRequest =
                new JobListRequest(1, "ASC", null, null, null, null, null, null);

        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<JobListReponse> getJobList = service.getJobListByParam(
                APICommon.getSign(APICommon.getApiKey(), JobListRequest.PATH_URL),
                APICommon.getAppId(),
                APICommon.getDeviceType(),
                jobListRequest.page,
                jobListRequest.Order,
                jobListRequest.Title,
                jobListRequest.Location,
                jobListRequest.Skill,
                jobListRequest.MinSalary,
                jobListRequest.FromSalary,
                jobListRequest.ToSalary);
        //Call<JobListReponse> getJobList = service.getJobList(url);
        getJobList.enqueue(new Callback<JobListReponse>() {
            @Override
            public void onResponse(Response<JobListReponse> response, Retrofit retrofit) {
                if (response.body().code == 200) {
                    if (response.body().result != null && response.body().result.data != null && response.body().result.data.size() > 0) {
                        adapter.addAll(response.body().result.data);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "(on failed): " + t.toString());
                Toast.makeText(getActivity(), getActivity().getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initUI(View view) {
        rlSearchBar = (RelativeLayout) view.findViewById(R.id.rlSearchBar);
        rlSearchBar.setVisibility(getArguments().getBoolean(KEY_SEACH) ? View.VISIBLE : View.GONE);
        rvListJob = (CRecyclerView) view.findViewById(R.id.rvListJob);
        rvListJob.setDivider();
        adapter = new JobListAdapter(getActivity(), new ArrayList<JobObject>());
        rvListJob.setAdapter(adapter);
        Utils.closeKeyboard(getActivity());
    }

}
