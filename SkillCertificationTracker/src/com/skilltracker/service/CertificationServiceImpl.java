package com.skilltracker.service;

import com.skilltracker.dao.CertificationDAO;
import com.skilltracker.dao.ICertificationDAO;
import com.skilltracker.exception.DataNotFoundException;
import com.skilltracker.model.ExpiredCertificateView;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic implementation for certifications.
 */
public class CertificationServiceImpl implements CertificationService {

    // Polymorphism (interface reference)
    private ICertificationDAO certificationDAO = new CertificationDAO();

    @Override
    public void issueCertification(com.skilltracker.model.Certification certification) {
        try {
            certificationDAO.addCertification(certification);
        } catch (SQLException e) {
            throw new RuntimeException("Error issuing certification");
        }
    }

    @Override
    public List<ExpiredCertificateView> viewExpiredCertificates()
            throws DataNotFoundException {

        try {
            List<ExpiredCertificateView> expired =
                certificationDAO.getExpiredCertificates();

            if (expired.isEmpty()) {
                throw new DataNotFoundException("No expired certificates found");
            }

            return expired;

        } catch (SQLException e) {
            throw new DataNotFoundException("Database error occurred");
        }
    }
}
