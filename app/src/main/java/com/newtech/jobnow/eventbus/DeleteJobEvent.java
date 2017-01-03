package com.newtech.jobnow.eventbus;

/**
 * Created by SANG on 9/12/2016.
 */
public class DeleteJobEvent {
    public int position;
    public int type;
    public DeleteJobEvent(int position, int type) {
        this.position = position;
        this.type = type;
    }
}
