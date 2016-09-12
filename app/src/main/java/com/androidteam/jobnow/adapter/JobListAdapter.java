package com.androidteam.jobnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.DetailJobsActivity;
import com.androidteam.jobnow.eventbus.DeleteJobEvent;
import com.androidteam.jobnow.models.JobObject;
import com.androidteam.jobnow.utils.Utils;
import com.ocpsoft.pretty.time.PrettyTime;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

/**
 * Created by manhi on 1/6/2016.
 */

public class JobListAdapter extends BaseRecyclerAdapter<JobObject, JobListAdapter.ViewHolder> {

    public static final String TAG = JobListAdapter.class.getSimpleName();
    public static final int NORMAL_TYPE = 0;
    public static final int SAVE_TYPE = 1;
    public static final int APPLY_TYPE = 2;
    private PrettyTime p;
    private int type;

    public JobListAdapter(Context context, List<JobObject> list, int type) {
        super(context, list);
        this.p = new PrettyTime();
        this.type = type;
    }

    public JobListAdapter(Context context, List<JobObject> list) {
        super(context, list);
        this.p = new PrettyTime();
        this.type = 0;
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
        private LinearLayout lnFeautured, lnJob;
        private TextView tvName, tvLocation, tvPrice, tvTime, tvCompanyName;
        private ImageView imgLogo;
        private ImageButton btnRemove;

        public ViewHolder(View view) {
            super(view);
            lnFeautured = (LinearLayout) view.findViewById(R.id.lnFeautured);
            lnJob = (LinearLayout) view.findViewById(R.id.lnJob);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvCompanyName = (TextView) view.findViewById(R.id.tvCompanyName);
            imgLogo = (ImageView) view.findViewById(R.id.imgLogo);
            btnRemove = (ImageButton) view.findViewById(R.id.btnRemove);
            btnRemove.setVisibility(type == 0 ? View.GONE : View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailJobsActivity.class);
                    int job_id = (type == NORMAL_TYPE ? list.get(getAdapterPosition()).id :
                            list.get(getAdapterPosition()).job_id);
                    intent.putExtra("jobId", job_id);
                    intent.putExtra("jobObject", list.get(getAdapterPosition()));
                    mContext.startActivity(intent);
                }
            });

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new DeleteJobEvent(getAdapterPosition(), type));
                }
            });
        }

        public void bindData(JobObject jobObject) {
            tvName.setText(jobObject.Title);
            tvLocation.setText(jobObject.Position);
            tvPrice.setText(jobObject.FromSalary + " - " + jobObject.ToSalary + " (USD)");
            tvTime.setText(mContext.getString(R.string.posted)+" "+p.format(new Date(Utils.getLongTime(jobObject.created_at))));
            tvCompanyName.setText(jobObject.CompanyName);
            Picasso.with(mContext).load(jobObject.CompanyLogo).placeholder(R.mipmap.img_logo_company).error(R.mipmap.img_logo_company).into(imgLogo);
        }

    }

}
