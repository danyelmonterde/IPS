package com.monterdev.repository;

import com.monterdev.model.Qrcode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface QrcodeRepository extends CrudRepository<Qrcode, Integer> {

    @Query(value = "TRUNCATE TABLE qrcode", nativeQuery = true)
    void truncateQrCodes();
}
