package com.newtech.jobnow.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SANG on 9/12/2016.
 */
public class DeleteJobRequest extends BaseRequest implements Serializable {
    private static final String PATH_SAVE_URL = "api/v1/jobs/postDeleteSaveJob";
    private static final String PATH_APPLY_URL = "api/v1/jobs/postDeleteAppliedJob";

    @SerializedName("JobSeekerID")
    int jobSeekerId;

    @SerializedName("JobID")
    int jobId;

    @SerializedName("ApiToken")
    String apiToken;

    @SerializedName("UserID")
    int userId;

    public DeleteJobRequest(int jobSeekerId, int jobId, String apiToken, int userId, int type) {
        super(type == 1 ? PATH_SAVE_URL : PATH_APPLY_URL);
        this.jobSeekerId = jobSeekerId;
        this.jobId = jobId;
        this.apiToken = apiToken;
        this.userId = userId;
    }
}
