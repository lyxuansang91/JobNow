package com.newtech.jobnow.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sang on 9/28/2016.
 */
public class NotificationResponse extends BaseResponse implements Serializable {
    public ArrayList<NotificationObject> result;
}
