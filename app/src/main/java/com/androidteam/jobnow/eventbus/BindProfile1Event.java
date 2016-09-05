package com.androidteam.jobnow.eventbus;

import com.androidteam.jobnow.models.UserModel;

/**
 * Created by manhi on 5/9/2016.
 */
public class BindProfile1Event {
    public UserModel userModel;

    public BindProfile1Event(UserModel userModel) {
        this.userModel = userModel;
    }
}
