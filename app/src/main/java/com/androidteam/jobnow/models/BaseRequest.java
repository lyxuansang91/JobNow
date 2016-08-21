package com.androidteam.jobnow.models;

import com.androidteam.jobnow.common.APICommon;

import java.io.Serializable;

/**
 * Created by SANG on 8/21/2016.
 */
public class BaseRequest implements Serializable {
    private String app_id;
    private int device_type;
    private String sign;

    public BaseRequest(String path_url) {
        app_id = APICommon.getAppId();
        device_type = APICommon.getDeviceType();
        sign = APICommon.getSign(APICommon.getApiKey(), path_url);
    }
}
