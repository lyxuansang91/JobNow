package com.androidteam.jobnow.models;

/**
 * Created by manhi on 6/9/2016.
 */
public class UpdateProfileRequest extends BaseRequest {
    public static final String PATH_URL = "api/v1/users/postRegisterSocialite";
    public String FullName;
    public String Email;
    public String Avatar;
    public String FB_id;
    public String ApiToken;
    public Integer UserID;

    public UpdateProfileRequest(String FullName, String Email, String FB_id, String ApiToken, Integer UserID) {
        super(PATH_URL);
        this.FullName = FullName;
        this.Email = Email;
        this.FB_id = FB_id;
        this.ApiToken = ApiToken;
        this.UserID = UserID;
    }
}
