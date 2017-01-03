package com.newtech.jobnow.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by manhi on 26/8/2016.
 */
public class JobObject implements Serializable {
    public Integer id;

    @SerializedName("JobID")
    public Integer job_id;

    public Integer CompanyID;
    public String Title;
    public String Position;
    public Integer Level;
    public String YearOfExperience;
    public String LocationID;
    public String FromSalary;
    public String ToSalary;
    public Integer CurrencyID;
    public Integer IsDisplaySalary;
    public String Description;
    public String Requirement;
    // public String CreateDate;
    public Integer IsActive;
    public String created_at;
    public String updated_at;
    public String LocationName;
    public String CompanyName;
    public String CompanyLogo;
    public String IndustryName;
    public String CurrencyName;
    public Integer CountUserApplyJob;
    @SerializedName("IsApplyJob")
    public boolean isApplyJob;

    @SerializedName("IsSaveJob")
    public boolean isSaveJob;
    public String ShareUrl;
    @SerializedName("Latitude")
    public Double latitude;

    @SerializedName("Longitude")
    public Double longtitude;

}
