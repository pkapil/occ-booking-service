package com.occ.booking.service;

import com.occ.booking.model.SaleProcDataLck;
import com.occ.booking.repo.SaleProcDataLckRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SAProcessorService {

    @Autowired
    SaleProcDataLckRepository saleProcDataLckRepository;


    public void performJob(UUID uuid) throws InterruptedException {
        List<SaleProcDataLck> saleProcDataLckList = null, saleProcDataLck1 = null;
        //1. Read first available Job mark as STARTED
        try {
            saleProcDataLckList = setStartedState(uuid);
        } catch (OptimisticLockingFailureException | StaleStateException e) {
            log.info("{} unable to acquire lock {} {} ", Thread.currentThread().getName(), "", e.getMessage());
        } catch (NoJobException e) {
            log.error("No jobs left in queue");
            return;
        } catch (Exception e) {
            log.info("{}  failed the {}", Thread.currentThread().getName(), e.getMessage());
        }
        //2. Do the actual Job
        log.info("{}  won and performing the  {} ", Thread.currentThread().getName(), saleProcDataLckList.stream().map(SaleProcDataLck::getStoreName).collect(Collectors.toSet()));
        Thread.sleep(new Random().nextInt(9001) + 1000);

        //3. Mark Job as COMPLETED
        try {
            saleProcDataLck1 = setCompleteState(saleProcDataLckList);
            log.info("{} {} Completed.", Thread.currentThread().getName(), saleProcDataLckList.stream().map(SaleProcDataLck::getStoreName).collect(Collectors.toSet()));
        } catch (Exception e) {
            log.info("{}  failed the {} {}", Thread.currentThread().getName(), saleProcDataLckList.stream().map(SaleProcDataLck::getStoreName).collect(Collectors.toSet()), e.getMessage());
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private List<SaleProcDataLck> setStartedState(UUID uuid) {
        List<SaleProcDataLck> saleProcDataLckList = saleProcDataLckRepository.findTop5ByStatus(SaleProcDataLck.processing_status.INIT);
        if (saleProcDataLckList == null || saleProcDataLckList.isEmpty()) {
            throw new NoJobException("No Job");
        }

        for (SaleProcDataLck saleProcDataLck : saleProcDataLckList)
            if (saleProcDataLck != null) {
                saleProcDataLck.setStatus(SaleProcDataLck.processing_status.STARTED);
                saleProcDataLck.setPerformedByThreadName(Thread.currentThread().getName());
                log.info("{}  attempted the  {}", Thread.currentThread().getName(), saleProcDataLck.getStoreName());

            }
        saleProcDataLckList = (List<SaleProcDataLck>) saleProcDataLckRepository.saveAll(saleProcDataLckList);
        return saleProcDataLckList;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private List<SaleProcDataLck> setCompleteState(List<SaleProcDataLck> saleProcDataLckList) {

        List<SaleProcDataLck> answer = new ArrayList<>();
        for (SaleProcDataLck saleProcDataLck : saleProcDataLckList) {
            Optional<SaleProcDataLck> bookData1 = saleProcDataLckRepository.findById(saleProcDataLck.getId());
            if (bookData1.isPresent()) {
                saleProcDataLck = bookData1.get();
                saleProcDataLck.setTimeStamp(Instant.now());
                saleProcDataLck.setStatus(SaleProcDataLck.processing_status.COMPLETED);
                answer.add(saleProcDataLck);
            }
        }
        if (answer.isEmpty()) {
            throw new NoJobException("No Job");
        }
        answer = (List<SaleProcDataLck>) saleProcDataLckRepository.saveAll(answer);
        return answer;

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
