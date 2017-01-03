package com.newtech.jobnow.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.acitvity.FilterActivity;
import com.newtech.jobnow.acitvity.MyApplication;
import com.newtech.jobnow.acitvity.NotificationActivity;
import com.newtech.jobnow.acitvity.SearchResultActivity;
import com.newtech.jobnow.adapter.JobListAdapter;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.eventbus.DeleteJobEvent;
import com.newtech.jobnow.eventbus.SaveJobEvent;
import com.newtech.jobnow.eventbus.SaveJobListEvent;
import com.newtech.jobnow.models.BaseResponse;
import com.newtech.jobnow.models.DeleteJobRequest;
import com.newtech.jobnow.models.JobListReponse;
import com.newtech.jobnow.models.JobListRequest;
import com.newtech.jobnow.models.JobObject;
import com.newtech.jobnow.utils.Utils;
import com.newtech.jobnow.widget.CRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaveJobListFragment extends Fragment {
    public static final String TAG = SaveJobListFragment.class.getSimpleName();

    public static int saved_job = 0;

    public SaveJobListFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance() {
        return new SaveJobListFragment();
    }

    //    private LinearLayout lnJob1, lnJob2, lnJob3;
    private CRecyclerView rvListJob;
    private JobListAdapter adapter;
    private TextView tvNumberJob;
    private SwipeRefreshLayout refresh;
    private boolean isCanNext = false;
    private boolean isProgessingLoadMore = false;
    private RelativeLayout imgFilter, imgBack;
    private int page = 1;
    private LinearLayout lnErrorView;


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
        isProgessingLoadMore = true;
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                Config.Pref, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        Call<JobListReponse> request = service.getSaveListJob(
                APICommon.getSign(APICommon.getApiKey(), "/api/v1/jobs/getSaveJob"),
                APICommon.getAppId(),
                APICommon.getDeviceType(),
                userId,
                token, page);
        request.enqueue(new Callback<JobListReponse>() {
            @Override
            public void onResponse(Response<JobListReponse> response, Retrofit retrofit) {
                refresh.setRefreshing(false);
                isProgessingLoadMore = false;
                JobListReponse jobList = response.body();
                if(jobList != null) {
                    if(jobList.code == 200) {
                        JobListReponse.JobListResult result = jobList.result;
                        if(result != null) {
                            tvNumberJob.setText(result.total + " saved job");
                            adapter.addAll(result.data);

                            Log.d(TAG, "save job list total:" + result.total);
                            saved_job = result.total;

                            if(page < result.last_page) {
                                page++;
                                isCanNext = true;
                            } else
                                isCanNext = false;
                            if(result.data.size() == 0)
                                isCanNext = false;

                            EventBus.getDefault().post(new SaveJobListEvent(saved_job));
                            if(result.total == 0) {
                                lnErrorView.setVisibility(View.VISIBLE);
                                rvListJob.setVisibility(View.GONE);
                            } else {
                                lnErrorView.setVisibility(View.GONE);
                                rvListJob.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if(jobList.code == 503) {
                        MyApplication.getInstance().getApiToken(new MyApplication.TokenCallback() {
                            @Override
                            public void onTokenSuccess() {
                                page = 1;
                                bindData();
                            }
                        });
                        Toast.makeText(getActivity(), jobList.message, Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                refresh.setRefreshing(false);
                isProgessingLoadMore = false;
                Toast.makeText(getActivity(), getString(R.string.error_connect),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void search(String title) {
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
        rvListJob = (CRecyclerView) view.findViewById(R.id.rvListJob);
        rvListJob.setDivider();
        adapter = new JobListAdapter(getActivity(), new ArrayList<JobObject>(), JobListAdapter.SAVE_TYPE);
        rvListJob.setAdapter(adapter);
        tvNumberJob = (TextView) view.findViewById(R.id.tvNumberJob);
        lnErrorView = (LinearLayout) view.findViewById(R.id.lnErrorView);
        imgFilter = (RelativeLayout) view.findViewById(R.id.imgFilter);
        imgBack = (RelativeLayout) view.findViewById(R.id.imgRing);

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

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
                if(Utils.isReadyForPullEnd(recyclerView) && isCanNext && !isProgessingLoadMore) {
                    bindData();
                }
            }
        });

        EditText edtSearch = (EditText) view.findViewById(R.id.edSearch);
        edtSearch.requestFocus();
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Subscribe
    public void onEvent(SaveJobEvent event) {
        adapter.clear();
        bindData();
    }

    @Subscribe
    public void onEvent(DeleteJobEvent event) {
        int type = event.type;
        if(type == JobListAdapter.SAVE_TYPE) {
            int job_id = adapter.getItembyPostion(event.position).job_id;
            final int position = event.position;
            APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                    Config.Pref, Context.MODE_PRIVATE);
            String token = sharedPreferences.getString(Config.KEY_TOKEN, "");
            int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
            Call<BaseResponse> call = service.deleteSaveJob(new DeleteJobRequest(userId, job_id,
                    token, userId, type));
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                    if(response.body() != null && response.body().code == 200) {
                        adapter.remove(position);
                        tvNumberJob.setText(adapter.getItemCount() + " saved job");
                        EventBus.getDefault().post(new SaveJobListEvent(adapter.getItemCount()));
                        if(adapter.getItemCount() == 0) {
                            lnErrorView.setVisibility(View.VISIBLE);
                            rvListJob.setVisibility(View.GONE);
                        } else {
                            lnErrorView.setVisibility(View.GONE);
                            rvListJob.setVisibility(View.VISIBLE);
                        }
                    }
                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getActivity(), getString(R.string.error_connect), Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "on attach");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "on detach");
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }
}
