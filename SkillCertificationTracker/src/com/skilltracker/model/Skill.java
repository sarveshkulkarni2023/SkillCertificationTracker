package com.skilltracker.model;

public class Skill {

    private int skillId;
    private String skillName;

    public Skill(String skillName) {
        this.skillName = skillName;
    }
//getters
    public int getSkillId() {
        return skillId;
    }

    public String getSkillName() {
        return skillName;
    }
}
