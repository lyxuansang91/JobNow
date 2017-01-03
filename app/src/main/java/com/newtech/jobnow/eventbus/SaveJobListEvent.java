package com.newtech.jobnow.eventbus;

/**
 * Created by sang on 9/27/2016.
 */
public class SaveJobListEvent {

    public int total;
    public SaveJobListEvent(int total) {
        this.total = total;
    }
}
