package com.occ.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class BookingService {

    @Autowired
    BookDataRepository bookDataRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void performJob(UUID uuid) throws InterruptedException {
        BookData bookData = bookDataRepository.findFirstByStatus(BookData.processing_status.INIT);
        if (bookData != null) {
            try {
                log.info("{}  performing the {} {} {}", Thread.currentThread().getName(), bookData.getJobName(), uuid);
                bookData.setTimeStamp(Instant.now());
                bookData.setStatus(BookData.processing_status.STARTED);
                bookData.setPerformedByThreadName(Thread.currentThread().getName());
                Thread.sleep(1000);
                bookData.setStatus(BookData.processing_status.COMPLETED);
                bookDataRepository.save(bookData);
            } catch (Exception e) {
                log.info("{}  failed the {} {} {}", Thread.currentThread().getName(), bookData.getJobName(), e.getMessage(), uuid);
            }
        } else {
            log.error("No job");
        }
    }

}
