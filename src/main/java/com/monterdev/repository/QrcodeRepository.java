package com.monterdev.repository;

import com.monterdev.model.Qrcode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrcodeRepository extends CrudRepository<Qrcode, Integer> {

    @Procedure
    void truncateQrCodes();
}
