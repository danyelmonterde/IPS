package com.monterdev.repository;

import com.monterdev.model.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;


public interface QrcodeRepository extends JpaRepository<Barcode, Integer> {

    @Procedure
    void truncateQrCodes();
}
