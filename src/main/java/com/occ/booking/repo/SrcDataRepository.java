package com.occ.booking.repo;

import com.occ.booking.model.SrcData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SrcDataRepository extends CrudRepository<SrcData,Long> {
    @Query(
            value = "SELECT * FROM SRC_DATA WHERE  ROWNUM >=:rowNum1 and ROWNUM <= :rowNum2",
            nativeQuery = true)
public List<SrcData> getRowsBetweenRowNum1AndRowNum2(Long rowNum1,Long rowNum2);
    @Query(
            value = "SELECT MAX(ROWNUM) FROM SRC_DATA",
            nativeQuery = true)
    public Long getMaxRowNumber();
}
