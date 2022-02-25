package com.monterdev.repository;

import com.monterdev.model.Barcode;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrcodeRepository extends CrudRepository<Barcode, Integer> {

    @Procedure
    void truncateQrCodes();
}
