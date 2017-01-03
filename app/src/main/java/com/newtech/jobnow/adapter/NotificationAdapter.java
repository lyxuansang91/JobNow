package com.newtech.jobnow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.newtech.jobnow.R;
import com.newtech.jobnow.models.NotificationObject;

import java.util.List;

/**
 * Created by sang on 9/28/2016.
 */

public class NotificationAdapter extends BaseRecyclerAdapter<NotificationObject, NotificationAdapter.ViewHolder> {

    public NotificationAdapter(Context context, List<NotificationObject> list) {
        super(context, list);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.notification_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvDescription, tvDate;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitleNotification);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescriptionNotification);
            tvDate = (TextView) itemView.findViewById(R.id.tvDateNotification);
        }

        public void bindData(int position) {
            NotificationObject notificationObject = getItembyPostion(position);
            if(notificationObject != null) {
                tvTitle.setText(notificationObject.title);
                tvDescription.setText(notificationObject.content);
                tvDate.setText(notificationObject.createDate);
            }
        }
    }
}
