package com.androidteam.jobnow.models;

import java.util.List;

/**
 * Created by manhi on 26/8/2016.
 */

public class JobLocationResponse extends BaseResponse {
    public List<JobLocationResult> result;

    public static class JobLocationResult {
        public Integer id;
        public String Name;
        public String ZipCode;
        public String CountryID;
        public Integer IsActive;
        public String Description;
        public String created_at;
        public String updated_at;
    }
}