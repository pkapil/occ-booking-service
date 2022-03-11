package com.occ.booking.service;

import com.occ.booking.config.AppProp;
import com.occ.booking.model.SaleExtrDataLck;
import com.occ.booking.model.SrcData;
import com.occ.booking.repo.SaleExtrDataLckRepository;
import com.occ.booking.repo.SrcDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class SAExtractorService {

    @Autowired
    SaleExtrDataLckRepository saleExtrDataLckRepository;

    @Autowired
    SrcDataRepository srcDataRepository;

    @Autowired
    AppProp appProp;


    public void performJob(UUID uuid) throws InterruptedException {

        SaleExtrDataLck saleExtrDataLck = null, SaleExtrDataLck1 = null;
        //1. Read first available Job mark as STARTED
        try {
            saleExtrDataLck = setStartedState(uuid);
        } catch (ConstraintViolationException e) {
            log.info("{} unable to acquire lock {} {} ", Thread.currentThread().getName(), "", e.getMessage());
        } catch (NoJobException e) {
            log.error("No jobs left in queue");
            return;
        } catch (Exception e) {
            log.info("{}  failed the {}", Thread.currentThread().getName(), e.getMessage());
        }
        //2. Do the actual Job
        log.info("{} won and performing.{}->{}", Thread.currentThread().getName(), saleExtrDataLck.getStartRowNo(), saleExtrDataLck.getEndRowNo());
        Thread.sleep(new Random().nextInt(9001) + 1000);

        List<SrcData> dataList
                 = srcDataRepository.getRowsBetweenRowNum1AndRowNum2(saleExtrDataLck.getStartRowNo(),saleExtrDataLck.getEndRowNo());
        log.info("****** --> "+dataList.size());

        //3. Mark Job as COMPLETED
        try {
            SaleExtrDataLck1 = setCompleteState(saleExtrDataLck);
            log.info("{} Completed.{}->{}", Thread.currentThread().getName(), saleExtrDataLck.getStartRowNo(), saleExtrDataLck.getEndRowNo());
        } catch (Exception e) {
            log.info("{} failed .{}->{}", Thread.currentThread().getName(), saleExtrDataLck.getStartRowNo(), saleExtrDataLck.getEndRowNo());
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private SaleExtrDataLck setStartedState(UUID uuid) {
        int increment = appProp.getSaExtrBatchSize();
        Long maxRowNoLowerBound = saleExtrDataLckRepository.getMaxRowNo(SaleExtrDataLck.processing_status.STARTED);
        maxRowNoLowerBound = maxRowNoLowerBound == null ? 0 : maxRowNoLowerBound;

        if (maxRowNoLowerBound == 0) {
            maxRowNoLowerBound = saleExtrDataLckRepository.getMaxRowNo(SaleExtrDataLck.processing_status.COMPLETED);
            maxRowNoLowerBound = maxRowNoLowerBound == null ? 0 : maxRowNoLowerBound;
        }

        Long srcMaxNum = srcDataRepository.getMaxRowNumber();
        srcMaxNum = srcMaxNum == null ? 0 : srcMaxNum;
        maxRowNoLowerBound=maxRowNoLowerBound+1;
        Long maxRowNoUpperBound = maxRowNoLowerBound+(increment-1);

        if (maxRowNoLowerBound >= srcMaxNum) {
            throw new RuntimeException("No More Jobs!");
        }
        if (maxRowNoLowerBound < srcMaxNum && maxRowNoLowerBound + (increment-1) > srcMaxNum) {
            maxRowNoUpperBound = srcMaxNum;
        }

        Optional<SaleExtrDataLck> saleExtrDataLckOpt = saleExtrDataLckRepository.findByStartRowNoAndEndRowNoAndStatus(maxRowNoLowerBound, maxRowNoUpperBound, SaleExtrDataLck.processing_status.INIT);
        SaleExtrDataLck saleExtrDataLck;
        if (saleExtrDataLckOpt.isPresent()) {
            saleExtrDataLck = saleExtrDataLckOpt.get();
            saleExtrDataLck.setStatus(SaleExtrDataLck.processing_status.STARTED);

        } else {
            saleExtrDataLck = SaleExtrDataLck.builder()
                    .status(SaleExtrDataLck.processing_status.STARTED)
                    .startRowNo(maxRowNoLowerBound)
                    .endRowNo(maxRowNoUpperBound)
                    .build();
        }
        saleExtrDataLck.setPerformedBy(Thread.currentThread().getName());
        saleExtrDataLck.setTimeStamp(Instant.now());
        saleExtrDataLckRepository.save(saleExtrDataLck);
        return saleExtrDataLck;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private SaleExtrDataLck setCompleteState(SaleExtrDataLck SaleExtrDataLckList) {
        SaleExtrDataLckList.setStatus(SaleExtrDataLck.processing_status.COMPLETED);
        return saleExtrDataLckRepository.save(SaleExtrDataLckList);
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
