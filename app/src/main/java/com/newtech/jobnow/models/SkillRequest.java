package com.newtech.jobnow.models;

/**
 * Created by manhi on 8/9/2016.
 */
public class SkillRequest extends BaseRequest {
    private static final String PATH_URL = "api/v1/skill/postEditSkill";
    public String ApiToken;
    public int UserID;
    public int[] Skill;

    public SkillRequest(String apiToken, int userID, int[] skill) {
        super(PATH_URL);
        ApiToken = apiToken;
        UserID = userID;
        Skill = skill;
    }
}
