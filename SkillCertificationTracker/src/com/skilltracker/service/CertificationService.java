package com.skilltracker.service;

import com.skilltracker.exception.DataNotFoundException;
import com.skilltracker.model.ExpiredCertificateView;

import java.util.List;

public interface CertificationService {

    void issueCertification(com.skilltracker.model.Certification certification);

    List<ExpiredCertificateView> viewExpiredCertificates()
            throws DataNotFoundException;
}
