package com.newtech.jobnow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.newtech.jobnow.R;
import com.newtech.jobnow.models.SkillResponse;

import java.util.List;

/**
 * Created by manhi on 1/6/2016.
 */

public class MySkillAdapter extends BaseRecyclerAdapter<SkillResponse.Skill, MySkillAdapter.ViewHolder> {
    public static final String TAG = MySkillAdapter.class.getSimpleName();

    public MySkillAdapter(Context context, List<SkillResponse.Skill> list) {
        super(context, list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_my_skills, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        super.onBindViewHolder(holder, position);
        holder.bindData(list.get(position));
    }

    @Override
    public SkillResponse.Skill getItembyPostion(int position) {
        return super.getItembyPostion(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;

        public ViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);

        }

        public void bindData(SkillResponse.Skill skill) {
            tvName.setText(skill.Name);
        }

    }

}
