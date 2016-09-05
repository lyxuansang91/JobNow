package com.androidteam.jobnow.models;

import java.util.List;

/**
 * Created by manhi on 27/8/2016.
 */
public class SkillResponse extends BaseResponse {
    public List<Skill> result;

    public class Skill {
        public Integer id;
        public String Name;
        public Integer IndustryID;
        public Integer IsActive;
        public String Description;
        public String created_at;
        public String updated_at;
        public boolean isChecked = false;
    }
}
