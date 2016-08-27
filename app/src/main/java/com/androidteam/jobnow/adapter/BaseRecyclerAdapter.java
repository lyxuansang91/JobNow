package com.androidteam.jobnow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manhi on 20/1/2016.
 */
public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public Context mContext;
    public List<T> list = new ArrayList<>();
    public final LayoutInflater layoutInflater;

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public BaseRecyclerAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(int position, T item) {
        list.set(position, item);
        this.notifyItemChanged(position);
    }

    public void setData(T item) {
        this.notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        list.remove(position);
        this.notifyItemRemoved(position);
    }

    public void add(int pos, T item) {
        list.add(pos, item);
        notifyItemInserted(pos);
    }

    public void add(T item) {
        list.add(item);
        notifyItemInserted(list.size() - 1);
    }


    public void addAll(List<T> listItems) {
        list.addAll(listItems);
        notifyDataSetChanged();
    }

    public T getItembyPostion(int position) {
        return list.get(position);
    }

    //    @Override
//    public void onBindViewHolder(VH holder, final int position) {
//        if (onItemClickListener != null && holder.itemView != null) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onItemClick(position);
//                }
//            });
//        }
//    }
    public abstract void onBindViewHolder(VH holder, final int position);

    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}