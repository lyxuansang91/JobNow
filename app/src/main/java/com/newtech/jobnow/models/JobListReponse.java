package com.newtech.jobnow.models;

import java.util.List;

/**
 * Created by manhi on 26/8/2016.
 */
public class JobListReponse extends BaseResponse {
    public JobListResult result;

    public static class JobListResult {
        public Integer total;
        public Integer per_page;
        public Integer current_page;
        public Integer last_page;
        public String next_page_url;
        public String prev_page_url;
        public String from;
        public String to;
        public List<JobObject> data;
    }
}
