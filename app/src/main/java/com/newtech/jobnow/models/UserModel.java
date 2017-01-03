package com.newtech.jobnow.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SANG on 8/21/2016.
 */
public class UserModel implements Serializable {

    public int id;
    @SerializedName("Username")
    public String username;

    @SerializedName("Email")
    public String email;

    @SerializedName("Fullname")
    public String fullname;

    @SerializedName("IsCompany")
    public int isCompany;

    @SerializedName("CreateDate")
    public String createDate;

    @SerializedName("IsEmailConfirmed")
    public int isEmailConfirmed;

    @SerializedName("PasswordSalt")
    public String passwordSalt;

    @SerializedName("fb_id")
    public String fb_id;

    @SerializedName("google_id")
    public String google_id;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("updated_at")
    public String updated_at;

    @SerializedName("ApiToken")
    public String apiToken;

    @SerializedName("Avatar")
    public String avatar;

    @SerializedName("BirthDay")
    public String birthDay;

    @SerializedName("PhoneNumber")
    public String phoneNumber;

    @SerializedName("CountryID")
    public String countryID;

    @SerializedName("Gender")
    public Integer gender;

    @SerializedName("CurriculumVitae")
    public String curriculumVitae;

    @SerializedName("Description")
    public String description;

    @SerializedName("CountryName")
    public String countryName;

    @SerializedName("PostalCode")
    public String postalCode;

}
