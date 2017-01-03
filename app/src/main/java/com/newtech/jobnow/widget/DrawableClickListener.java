package com.newtech.jobnow.widget;

/**
 * Created by manhi on 5/1/2016.
 */
public interface DrawableClickListener {
    public static enum DrawablePosition {TOP, BOTTOM, LEFT, RIGHT};

    public void onClick(DrawablePosition target);
}
