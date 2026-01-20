package com.skilltracker.dao;

import com.skilltracker.model.Skill;
import com.skilltracker.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SkillDAO {

    public void addSkill(Skill skill) throws SQLException {
        String sql =
            "INSERT INTO skills(skill_name) VALUES (?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, skill.getSkillName());
            ps.executeUpdate();
        }
    }
}
