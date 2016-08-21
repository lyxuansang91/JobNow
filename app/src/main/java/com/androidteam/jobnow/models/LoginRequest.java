package com.androidteam.jobnow.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SANG on 8/21/2016.
 */
public class LoginRequest extends BaseRequest implements Serializable {

    private static final String PATH_URL = "/api/v1/users/postLogin";

    @SerializedName("Email")
    private String email;

    @SerializedName("Password")
    private String password;

    public LoginRequest(String email, String password) {
        super(PATH_URL);
        this.email = email;
        this.password = password;
    }
}
