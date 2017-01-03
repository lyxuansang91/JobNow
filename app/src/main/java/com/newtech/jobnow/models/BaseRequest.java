package com.newtech.jobnow.models;

import com.newtech.jobnow.common.APICommon;

import java.io.Serializable;

/**
 * Created by SANG on 8/21/2016.
 */
public class BaseRequest implements Serializable {
    public String app_id;
    public int device_type;
    public String sign;

    public BaseRequest(String path_url) {
        app_id = APICommon.getAppId();
        device_type = APICommon.getDeviceType();
        sign = APICommon.getSign(APICommon.getApiKey(), path_url);
    }

    public BaseRequest() {

    }
}
