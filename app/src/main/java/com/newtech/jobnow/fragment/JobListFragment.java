package com.newtech.jobnow.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.acitvity.MyApplication;
import com.newtech.jobnow.acitvity.SearchResultActivity;
import com.newtech.jobnow.adapter.JobListAdapter;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.models.JobListReponse;
import com.newtech.jobnow.models.JobListRequest;
import com.newtech.jobnow.models.JobObject;
import com.newtech.jobnow.utils.Utils;
import com.newtech.jobnow.widget.CRecyclerView;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobListFragment extends Fragment {
    private String TAG = JobListFragment.class.getSimpleName();
    public static String KEY_SEACH = "hasSearch";
    public static String KEY_JOB = "key_job";
    private String ASC = "ASC";
    private String DESC = "DESC";
    private Spinner spnSortBy;
    private String sort = ASC;
    private int page = 1;
    private boolean isCanNext = false;
    private boolean isProgessingLoadMore = false;
    private JobListRequest jobListRequest = null;
    private LinearLayout lnErrorView;

    public JobListFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(boolean hasSearch) {
        return getInstance(hasSearch, null);
    }

    public static Fragment getInstance(boolean hasSearch, JobListRequest request) {
        Fragment fragment = new JobListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_SEACH, hasSearch);
        bundle.putSerializable(KEY_JOB, request);
        fragment.setArguments(bundle);
        return fragment;
    }

    //    private LinearLayout lnJob1, lnJob2, lnJob3;
    private RelativeLayout rlSearchBar;
    private CRecyclerView rvListJob;
    private JobListAdapter adapter;
    private TextView tvNumberJob;
    private SwipeRefreshLayout refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_job_list, container, false);
        initUI(rootView);
        //bindData();
        event();
        return rootView;
    }

    private void event() {
        spnSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sort = ASC;
                } else {
                    sort = DESC;
                }
                adapter.clear();
                page = 1;
                bindData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sort = ASC;
                adapter.clear();
                page = 1;
                bindData();
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(true);
                adapter.clear();
                page = 1;
                bindData();
            }
        });

        rvListJob.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Utils.isReadyForPullEnd(recyclerView) && isCanNext && !isProgessingLoadMore) {
                    bindData();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void bindData() {

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading...", "Please wait", true, false);
        isProgessingLoadMore = true;

        Bundle bundle = getArguments();
        if (bundle != null) {
            jobListRequest = (JobListRequest) bundle.getSerializable(KEY_JOB);
        }
        Log.d(TAG, "job list request: " + jobListRequest);
        if (jobListRequest == null)
            jobListRequest = new JobListRequest(page, sort, null, null, null, null,
                    null, null, null);

        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<JobListReponse> getJobList = service.getJobListByParam(
                APICommon.getSign(APICommon.getApiKey(), JobListRequest.PATH_URL),
                APICommon.getAppId(),
                APICommon.getDeviceType(),
                page,
                jobListRequest.Order,
                jobListRequest.Title,
                jobListRequest.Location,
                jobListRequest.Skill,
                jobListRequest.MinSalary,
                jobListRequest.FromSalary,
                jobListRequest.ToSalary,
                jobListRequest.industryID);
        getJobList.enqueue(new Callback<JobListReponse>() {
            @Override
            public void onResponse(Response<JobListReponse> response, Retrofit retrofit) {
                isProgessingLoadMore = false;
                refresh.setRefreshing(false);
                try {
                    progressDialog.dismiss();
                    if (response.body() != null && response.body().code == 200) {
                        if (response.body().result != null && response.body().result.data != null
                                && response.body().result.data.size() > 0) {
                            adapter.addAll(response.body().result.data);
                            tvNumberJob.setText(getString(R.string.number_job, response.body().result.total));
                            if (page < response.body().result.last_page) {
                                page++;
                                isCanNext = true;
                            } else {
                                isCanNext = false;
                            }
                        } else {
                            isCanNext = false;
                        }
                    }

                    if(adapter.getItemCount() == 0) {
                        lnErrorView.setVisibility(View.VISIBLE);
                        rvListJob.setVisibility(View.GONE);
                    } else {
                        lnErrorView.setVisibility(View.GONE);
                        rvListJob.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                refresh.setRefreshing(false);
                isProgessingLoadMore = false;
                progressDialog.dismiss();
                Log.d(TAG, "(on failed): " + t.toString());
                Toast.makeText(getActivity(), getActivity().getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void search(String title) {
        Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
        if (title.equals(""))
            title = null;
        JobListRequest request = new JobListRequest(1, "ASC", title, null, null,
                null, null, null, null);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SearchResultActivity.KEY_JOB, request);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    private void initUI(View view) {
        rlSearchBar = (RelativeLayout) view.findViewById(R.id.rlSearchBar);
        rlSearchBar.setVisibility(getArguments().getBoolean(KEY_SEACH) ? View.VISIBLE : View.GONE);
        rvListJob = (CRecyclerView) view.findViewById(R.id.rvListJob);
        rvListJob.setDivider();
        adapter = new JobListAdapter(getActivity(), new ArrayList<JobObject>());
        rvListJob.setAdapter(adapter);
        tvNumberJob = (TextView) view.findViewById(R.id.tvNumberJob);
        lnErrorView = (LinearLayout) view.findViewById(R.id.lnErrorView);


        spnSortBy = (Spinner) view.findViewById(R.id.spnSortBy);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        Utils.closeKeyboard(getActivity());

    }

}
