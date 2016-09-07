package com.androidteam.jobnow.models;

import com.androidteam.jobnow.common.APICommon;

/**
 * Created by manhi on 7/9/2016.
 */
public class ExperienceRequest extends BaseRequest {
    public static final int ADD = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    public static final String PATH_URL_ADD = "api/v1/jobseekerexperience/postAddJobSeekerExperience";
    public static final String PATH_URL_UPDATE = "api/v1/jobseekerexperience/postUpdateJobSeekerExperience";
    public static final String PATH_URL_DELETE = "api/v1/jobseekerexperience/postAddJobSeekerExperience";

    public Integer JobSeekerID;
    public String CompanyName;
    public String PositionName;
    public String Description;
    public String ApiToken;
    public int UserID;
    public int ExperienceID;

    public ExperienceRequest(Integer jobSeekerID, String companyName, String positionName, String description, String apiToken, int userID) {
        super(PATH_URL_ADD);
        JobSeekerID = jobSeekerID;
        CompanyName = companyName;
        PositionName = positionName;
        Description = description;
        ApiToken = apiToken;
        UserID = userID;
    }

    public ExperienceRequest(Integer jobSeekerID, String companyName, String positionName, String description, String apiToken, int userID, int experienceID, int type) {
        if (type == UPDATE) {
            sign = APICommon.getSign(APICommon.getApiKey(), PATH_URL_UPDATE);
        } else if (type == DELETE) {
            sign = APICommon.getSign(APICommon.getApiKey(), PATH_URL_DELETE);
        } else {
            sign = APICommon.getSign(APICommon.getApiKey(), PATH_URL_ADD);
        }
        JobSeekerID = jobSeekerID;
        CompanyName = companyName;
        PositionName = positionName;
        Description = description;
        ApiToken = apiToken;
        ExperienceID = experienceID;
        UserID = userID;
        app_id = APICommon.getAppId();
        device_type = APICommon.getDeviceType();
    }
}
