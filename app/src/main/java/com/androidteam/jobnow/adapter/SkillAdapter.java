package com.androidteam.jobnow.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.models.SkillResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manhi on 1/6/2016.
 */

public class SkillAdapter extends BaseRecyclerAdapter<SkillResponse.Skill, SkillAdapter.ViewHolder> {
    public static final String TAG = SkillAdapter.class.getSimpleName();

    public SkillAdapter(Context context, List<SkillResponse.Skill> list) {
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
                SkillResponse.Skill skill = getItembyPostion(position);
                if(skill != null) {
                    skill.isChecked = isChecked;
                    setData(position, skill);
                }
                if (isChecked) {
                    holder.checkBox.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                } else {
                    holder.checkBox.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                }
            }
        });
    }

    @Override
    public SkillResponse.Skill getItembyPostion(int position) {
        return super.getItembyPostion(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);

        }

        public void bindData(SkillResponse.Skill skill) {
            checkBox.setText(skill.Name);
        }

    }

}
