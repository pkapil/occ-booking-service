package com.occ.booking;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class BookingService {

    @Autowired
    BookDataRepository bookDataRepository;


    public void performJob(UUID uuid) throws InterruptedException {
        BookData bookData = null, bookData1 = null;
        try {
            bookData = setStartedState(uuid);
        } catch (OptimisticLockingFailureException | StaleStateException e) {
            log.info("{} unable to acquire lock {} {} ", Thread.currentThread().getName(), "", e.getMessage());
        } catch (NoJobException e){
            log.error("No jobs left in queue");
            return;
        }catch (Exception e) {
            log.info("{}  failed the {}", Thread.currentThread().getName(), e.getMessage());
        }
        log.info("{}  won and performing the  {} ", Thread.currentThread().getName(), bookData.getJobName());
        Thread.sleep(1000);

        try {
            bookData1 = setCompleteState(bookData);
            log.info("{} {} Completed.", Thread.currentThread().getName(),bookData.getJobName());
        } catch (Exception e) {
            log.info("{}  failed the {} {}", Thread.currentThread().getName(), bookData1.getJobName(), e.getMessage());
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private BookData setStartedState(UUID uuid) {
        BookData bookData = bookDataRepository.findFirstByStatus(BookData.processing_status.INIT);
        if (bookData != null) {
            bookData.setStatus(BookData.processing_status.STARTED);
            bookData.setPerformedByThreadName(Thread.currentThread().getName());
            log.info("{}  attempted the  {}", Thread.currentThread().getName(), bookData.getJobName());
            bookData = bookDataRepository.save(bookData);
            return bookData;
        } else {
            throw new NoJobException("No Job");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private BookData setCompleteState(BookData bookData) {
        Optional<BookData> bookData1 = bookDataRepository.findById(bookData.getId());
        if (bookData1.isPresent()) {
            bookData = bookData1.get();
            bookData.setTimeStamp(Instant.now());
            bookData.setStatus(BookData.processing_status.COMPLETED);
            bookDataRepository.save(bookData);
            return bookData;
        } else {
            throw new NoJobException("No Job");
        }
    }

    class NoJobException extends RuntimeException {
        public NoJobException() {
            super();
        }

        public NoJobException(String message, Throwable cause) {
            super(message, cause);
        }

        public NoJobException(String message) {
            super(message);
        }

        @Override
        public String getMessage() {
            return super.getMessage();
        }
    }

}
