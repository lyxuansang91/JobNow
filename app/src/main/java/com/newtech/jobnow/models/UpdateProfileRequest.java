package com.newtech.jobnow.models;

/**
 * Created by manhi on 6/9/2016.
 */
public class UpdateProfileRequest extends BaseRequest {
    public static final String PATH_URL = "api/v1/users/postRegisterSocialite";
    public String FullName;
    public String Email;
    public String BirthDay;
    public int Gender;
    public int PostalCode;
    public String Description;
    public String PhoneNumber;
    public String CountryID;
    public String FB_id;
    public String ApiToken;
    public Integer UserID;


    public UpdateProfileRequest(String fullName, String email, String birthDay, int gender,
                                int postalCode, String description, String phoneNumber,
                                String FB_id, String apiToken, Integer userID, String countryID) {
        super(PATH_URL);
        FullName = fullName;
        Email = email;
        BirthDay = birthDay;
        Gender = gender;
        PostalCode = postalCode;
        Description = description;
        PhoneNumber = phoneNumber;
        this.FB_id = FB_id;
        ApiToken = apiToken;
        UserID = userID;
        CountryID = countryID;
    }
}
