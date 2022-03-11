package com.occ.booking;

import com.occ.booking.config.AppProp;
import com.occ.booking.model.SaleProcDataLck;
import com.occ.booking.model.SrcData;
import com.occ.booking.repo.SaleProcDataLckRepository;
import com.occ.booking.repo.SrcDataRepository;
import com.occ.booking.service.SAExtractorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@Slf4j
public class OCCBookingApplication implements CommandLineRunner {

    @Autowired
    SaleProcDataLckRepository saleProcDataLckRepository;

    @Autowired
    SrcDataRepository srcDataRepository;

    @Autowired
    AppProp appProp;

    public static void main(String[] args) {
        SpringApplication.run(OCCBookingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i < appProp.getSaProcDataSize(); i++)
            saleProcDataLckRepository.save(SaleProcDataLck.builder()
                    .storeName("job" + i)
                    .status(SaleProcDataLck.processing_status.INIT)
                    .timeStamp(Instant.now())
                    .build());

        for (int i = 0; i < appProp.getSaExtrDataSize(); i++)
            srcDataRepository.save(SrcData.builder()
                    .timeStamp(Instant.now())
                    .binary(new String("TEST").getBytes(StandardCharsets.UTF_8))
                    .build());

    }
}
