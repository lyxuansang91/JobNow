package com.newtech.jobnow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtech.jobnow.R;
import com.newtech.jobnow.eventbus.EditExperienceEvent;
import com.newtech.jobnow.models.ExperienceResponse;
import com.ocpsoft.pretty.time.PrettyTime;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by manhi on 1/6/2016.
 */

public class ExperienceAdapter extends BaseRecyclerAdapter<ExperienceResponse.Experience, ExperienceAdapter.ViewHolder> {
    public static final String TAG = ExperienceAdapter.class.getSimpleName();
    private PrettyTime p;

    public ExperienceAdapter(Context context, List<ExperienceResponse.Experience> list) {
        super(context, list);
        this.p = new PrettyTime();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_experience, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bindData(list.get(position));
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EditExperienceEvent(list.get(position)));
            }
        });
    }

    @Override
    public ExperienceResponse.Experience getItembyPostion(int position) {
        return super.getItembyPostion(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCompanyName, tvLocation, tvDescription;
        private ImageView imgEdit;

        public ViewHolder(View view) {
            super(view);
            tvCompanyName = (TextView) view.findViewById(R.id.tvCompanyName);
            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            imgEdit = (ImageView) view.findViewById(R.id.imgEdit);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(mContext, DetailJobsActivity.class);
//                    mContext.startActivity(intent);
                }
            });
        }

        public void bindData(ExperienceResponse.Experience experience) {
            tvCompanyName.setText(experience.CompanyName);
            tvLocation.setText(experience.PositionName);
            tvDescription.setText(experience.Description);
        }

    }

}
