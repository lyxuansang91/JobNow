package com.newtech.jobnow.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sang on 10/4/2016.
 */
public class TokenRequest extends BaseRequest {
    private static final String PATH_URL = "/api/v1/users/getToken";

    @SerializedName("Email")
    private String email;

    @SerializedName("UserID")
    private int user_id;

    public TokenRequest(int user_id, String email) {
        super(PATH_URL);
        this.email = email;
        this.user_id = user_id;
    }

}
