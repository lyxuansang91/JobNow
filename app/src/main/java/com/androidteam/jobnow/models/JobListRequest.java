package com.androidteam.jobnow.models;

import java.io.Serializable;

/**
 * Created by manhi on 26/8/2016.
 */
public class JobListRequest extends BaseRequest implements Serializable {
    public static final String PATH_URL = "api/v1/jobs/getListJob";
    public int page;
    public String Order;
    public String Title;
    public String Location;
    public String Skill;
    public double MinSalary;
    public double FromSalary;
    public double ToSalary;

    public JobListRequest(int page, String Order, String Title, String Location, String Skill, double MinSalary, double FromSalary, double ToSalary) {
        super(PATH_URL);
        this.page = page;
        this.Order = Order;
        this.Title = Title;
        this.Location = Location;
        this.Skill = Skill;
        this.MinSalary = MinSalary;
        this.FromSalary = FromSalary;
        this.ToSalary = ToSalary;
    }
}
