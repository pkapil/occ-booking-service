package com.occ.booking.repo;

import com.occ.booking.model.SaleProcDataLck;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SaleProcDataLckRepository extends CrudRepository<SaleProcDataLck,Long> {

    public SaleProcDataLck findFirstByStatus(SaleProcDataLck.processing_status status);
    public List<SaleProcDataLck> findTop5ByStatus(SaleProcDataLck.processing_status status);

}
