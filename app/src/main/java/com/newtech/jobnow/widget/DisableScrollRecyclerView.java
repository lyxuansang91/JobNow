package com.newtech.jobnow.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by ANHTH on 06-Jun-16.
 */

public class DisableScrollRecyclerView extends RecyclerView {
    public DisableScrollRecyclerView(Context context) {
        super(context);
    }
    public DisableScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public DisableScrollRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

}
