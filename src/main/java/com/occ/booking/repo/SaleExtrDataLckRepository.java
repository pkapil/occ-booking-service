package com.occ.booking.repo;

import com.occ.booking.model.SaleExtrDataLck;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SaleExtrDataLckRepository extends CrudRepository<SaleExtrDataLck,Long> {
     @Query(value = "SELECT max(endRowNo) FROM SaleExtrDataLck S where S.status=:status")
    public Long getMaxRowNo(SaleExtrDataLck.processing_status status);

     public Optional<SaleExtrDataLck> findByStartRowNoAndEndRowNoAndStatus(Long startRowNo, Long endRowNo, SaleExtrDataLck.processing_status status);


}
