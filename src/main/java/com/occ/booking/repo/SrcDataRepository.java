package com.occ.booking.repo;

import com.occ.booking.model.SrcData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SrcDataRepository extends CrudRepository<SrcData,Long> {
    @Query(
            value = "select * from (select row_number() over() as rownum,* from src_data sd)  as bb\n" +
                    "where bb.rownum >= :rowNum1 and bb.rownum <= :rowNum2",
            nativeQuery = true)
public List<SrcData> getRowsBetweenRowNum1AndRowNum2(Long rowNum1,Long rowNum2);
    @Query(
            value = "select max(rownum ) from (select row_number() over() as rownum,* from src_data sd) as bb",
            nativeQuery = true)
    public Long getMaxRowNumber();
}
