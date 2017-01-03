package com.newtech.jobnow.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by manhi on 2/6/2016.
 */

public class CRecyclerView extends RecyclerView {
    public CRecyclerView(Context context) {
        super(context);
        init();
    }

    public CRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        this.setLayoutManager(layoutManager);
        this.setHasFixedSize(true);
        this.setItemAnimator(new DefaultItemAnimator());

    }

    public void setDivider() {
        this.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }
}
