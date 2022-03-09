package com.occ.booking;

import org.springframework.data.repository.CrudRepository;

public interface BookDataRepository extends CrudRepository<BookData,Long> {

    public BookData findFirstByStatus(BookData.processing_status status);

}
