package com.androidteam.jobnow.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SANG on 8/21/2016.
 */
public class LoginResponse extends BaseResponse implements Serializable {

    public UserModel result;

}
