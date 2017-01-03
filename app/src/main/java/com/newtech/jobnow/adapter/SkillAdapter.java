package com.newtech.jobnow.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.newtech.jobnow.R;
import com.newtech.jobnow.models.SkillResponse;

import java.util.List;

/**
 * Created by manhi on 1/6/2016.
 */

public class SkillAdapter extends BaseRecyclerAdapter<SkillResponse.Skill, SkillAdapter.ViewHolder> {
    public static final String TAG = SkillAdapter.class.getSimpleName();
    private boolean onBind;

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
        onBind = true;
        holder.bindData(list.get(position));
        onBind = false;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!onBind) {
                        SkillResponse.Skill skill = getItembyPostion(getAdapterPosition());
                        Log.d("SkillAdapter", "skill:" + skill);
                        if (skill != null) {
                            skill.isSelected = isChecked ? 1 : 0;
                            setData(getAdapterPosition(), skill);
                        }
                    }
                }
            });
        }

        public void bindData(SkillResponse.Skill skill) {
            Log.d(TAG, "skill bind data:" + skill.Name);
            checkBox.setText(skill.Name);
            if (skill.isSelected == 1) {
                checkBox.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                checkBox.setChecked(true);
            } else {
                checkBox.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                checkBox.setChecked(false);
            }
            //onBind = false;
        }

    }

}
