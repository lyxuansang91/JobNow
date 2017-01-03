package com.newtech.jobnow.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by manhi on 21/8/2016.
 */
public class RegisterFBRequest extends BaseRequest implements Serializable {
    private static final String PATH_URL = "api/v1/users/postRegisterSocialite";
    @SerializedName("FullName")
    private String fullName;

    @SerializedName("Email")
    private String email;

    @SerializedName("Avatar")
    private String avatar;

    @SerializedName("FB_id")
    private String fb_id;

    public RegisterFBRequest(String fullName, String email, String avatar, String fb_id) {
        super(PATH_URL);
        this.fullName = fullName;
        this.email = email;
        this.avatar = avatar;
        this.fb_id = fb_id;
    }
}
