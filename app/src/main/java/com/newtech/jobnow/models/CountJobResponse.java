package com.newtech.jobnow.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sang on 9/22/2016.
 */
public class CountJobResponse extends BaseResponse implements Serializable {
    @SerializedName("result")
    public int result;
}
