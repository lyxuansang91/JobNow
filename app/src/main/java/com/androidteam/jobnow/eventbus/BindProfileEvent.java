package com.androidteam.jobnow.eventbus;

import com.androidteam.jobnow.models.UserModel;

/**
 * Created by manhi on 5/9/2016.
 */
public class BindProfileEvent {
    public UserModel userModel;

    public BindProfileEvent(UserModel userModel) {
        this.userModel = userModel;
    }
}
