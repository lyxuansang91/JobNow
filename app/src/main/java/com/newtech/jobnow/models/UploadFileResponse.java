package com.newtech.jobnow.models;

/**
 * Created by manhi on 31/8/2016.
 */
public class UploadFileResponse extends BaseResponse {
    public UploadFileResult result;

    public static class UploadFileResult {
        public String img_url;
        public String avatar;
    }
}
