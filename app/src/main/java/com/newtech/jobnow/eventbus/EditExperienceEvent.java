package com.newtech.jobnow.eventbus;

import com.newtech.jobnow.models.ExperienceResponse;

/**
 * Created by manhi on 7/9/2016.
 */
public class EditExperienceEvent {
    public ExperienceResponse.Experience experience;

    public EditExperienceEvent(ExperienceResponse.Experience experience) {
        this.experience = experience;
    }
}
