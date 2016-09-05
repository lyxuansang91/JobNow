package com.androidteam.jobnow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.models.JobLocationResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manhi on 1/6/2016.
 */

public class JobLocationAdapter extends BaseRecyclerAdapter<JobLocationResponse.JobLocationResult, JobLocationAdapter.ViewHolder> {
    public static final String TAG = JobLocationAdapter.class.getSimpleName();

    public JobLocationAdapter(Context context, List<JobLocationResponse.JobLocationResult> list) {
        super(context, list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_job_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        super.onBindViewHolder(holder, position);
        holder.bindData(list.get(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JobLocationResponse.JobLocationResult result = getItembyPostion(position);
                if(result != null) {
                    result.isChecked = isChecked;
                    setData(position, result);
                }
            }
        });
    }

    @Override
    public JobLocationResponse.JobLocationResult getItembyPostion(int position) {
        return super.getItembyPostion(position);
    }

    public boolean isChecked(int position) {
        return getItembyPostion(position).isChecked;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        }

        public void bindData(JobLocationResponse.JobLocationResult locationResult) {
            checkBox.setText(locationResult.Name);
        }

    }

}
