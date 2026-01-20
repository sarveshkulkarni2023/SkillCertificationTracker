package com.skilltracker.dao;

import com.skilltracker.model.Certification;
import com.skilltracker.model.ExpiredCertificateView;

import java.sql.SQLException;
import java.util.List;

public interface ICertificationDAO {

    void addCertification(Certification cert) throws SQLException;

   
    List<ExpiredCertificateView> getExpiredCertificates() throws SQLException;
}
