package com.newtech.jobnow.models;

import java.util.ArrayList;

/**
 * Created by manhi on 26/8/2016.
 */

public class JobLocationResponse extends BaseResponse {
    public ArrayList<JobLocation> result;

    public static class JobLocation {
        public Integer id;
        public String Name;
        public String ZipCode;
        public String CountryID;
        public Integer IsActive;
        public String Description;
        public String created_at;
        public String updated_at;
        public boolean isChecked = false;
    }
}