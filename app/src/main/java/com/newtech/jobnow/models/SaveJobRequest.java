package com.newtech.jobnow.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SANG on 9/11/2016.
 */
public class SaveJobRequest extends BaseRequest implements Serializable {
    private static final String PATH_URL = "api/v1/jobs/postSaveJob";

    @SerializedName("JobID")
    public int jobId;

    @SerializedName("JobSeekerID")
    public int jobSeekerId;

    @SerializedName("UserID")
    public int userId;

    @SerializedName("ApiToken")
    public String apiToken;

    public SaveJobRequest(int jobId, int jobSeekerId, int userId, String apiToken) {
        super(PATH_URL);
        this.jobId = jobId;
        this.jobSeekerId = jobSeekerId;
        this.userId = userId;
        this.apiToken = apiToken;
    }
}
