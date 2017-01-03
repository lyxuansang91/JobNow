package com.newtech.jobnow.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by manhi on 21/8/2016.
 */
public class RegisterRequest extends BaseRequest implements Serializable {
    private static final String PATH_URL = "api/v1/users/postRegister";
    @SerializedName("FullName")
    private String fullName;
    @SerializedName("PhoneNumber")
    private String phoneNumber;
    @SerializedName("Email")
    private String email;
    @SerializedName("Password")
    private String password;

    public RegisterRequest(String fullName, String phoneNumber, String email, String password) {
        super(PATH_URL);
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
