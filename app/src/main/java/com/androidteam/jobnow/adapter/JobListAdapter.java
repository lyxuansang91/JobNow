package com.androidteam.jobnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.DetailJobsActivity;
import com.androidteam.jobnow.models.JobObject;

import java.util.List;

/**
 * Created by manhi on 1/6/2016.
 */

public class JobListAdapter extends BaseRecyclerAdapter<JobObject, JobListAdapter.ViewHolder> {
    public static final String TAG = JobListAdapter.class.getSimpleName();

    public JobListAdapter(Context context, List<JobObject> list) {
        super(context, list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_job, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        super.onBindViewHolder(holder, position);
        holder.bindData(list.get(position));
    }

    @Override
    public JobObject getItembyPostion(int position) {
        return super.getItembyPostion(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailJobsActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }

        public void bindData(JobObject jobObject) {

        }

    }

}
