package com.skilltracker.service;

import com.skilltracker.dao.CertificationDAO;
import com.skilltracker.exception.DuplicateSkillException;
import com.skilltracker.exception.ExpiredCertificationException;
import com.skilltracker.model.Certification;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;
import java.time.LocalDate;

public class CertificationService {

    private CertificationDAO dao = new CertificationDAO();

    public void assignCertification(Certification cert)
            throws DuplicateSkillException,
                   ExpiredCertificationException,
                   SQLException {

        if (cert.getExpiryDate().isBefore(LocalDate.now())) {
            throw new ExpiredCertificationException(
                "Expiry date already passed"
            );
        }

        try {
            dao.addCertification(cert);
        }
        catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateSkillException(
                "Student already has this skill"
            );
        }
    }
}
