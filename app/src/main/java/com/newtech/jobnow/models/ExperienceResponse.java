package com.newtech.jobnow.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by manhi on 7/9/2016.
 */
public class ExperienceResponse extends BaseResponse {
    public List<Experience> result;

    public static class Experience implements Serializable {
        public Integer id;
        public Integer JobSeekerID;
        public String CompanyName;
        public String PositionName;
        public String Description;
        public String created_at;
        public String updated_at;
    }
}
