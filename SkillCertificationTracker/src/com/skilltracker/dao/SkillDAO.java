package com.skilltracker.dao;

import com.skilltracker.util.DBUtil;

import java.sql.*;

public class SkillDAO {

    public int getOrCreateSkill(String skillName) throws SQLException {

        String select =
            "SELECT skill_id FROM skills WHERE skill_name = ?";
        String insert =
            "INSERT INTO skills(skill_name) VALUES (?)";

        try (Connection con = DBUtil.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(select)) {
                ps.setString(1, skillName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            try (PreparedStatement ps =
                     con.prepareStatement(insert,
                             Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, skillName);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
