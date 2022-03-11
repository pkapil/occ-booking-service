package com.occ.booking.controller;


import com.occ.booking.config.AppProp;
import com.occ.booking.service.SAExtractorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@Slf4j
public class BookingController {

    @Autowired
    com.occ.booking.service.SAProcessorService SAProcessorService;

    @Autowired
    SAExtractorService saExtractorService;

    @Autowired
    AppProp appProp;

    Runnable runnableProc = () -> {
        UUID uuid = UUID.randomUUID();
        try {
            Thread.sleep(new Random().nextInt(900) + 100);
            SAProcessorService.performJob(uuid);
        } catch (Exception e) {
            log.error("{} failed.", Thread.currentThread().getName());
        }
    };
    Runnable runnableExtract = () -> {
        UUID uuid = UUID.randomUUID();
        try {
            Thread.sleep(new Random().nextInt(900) + 100);
            saExtractorService.performJob(uuid);
        } catch (Exception e) {
            log.error("{} failed.", Thread.currentThread().getName());
        }
    };

    @GetMapping("/startp")
    public ResponseEntity<?> testMethod() {
        ExecutorService executorService = Executors.newFixedThreadPool(5, new CustomizableThreadFactory("alpha-"+new Random().nextInt(1000)));
        for (int i = 0; i < appProp.getEndpointThreadsCountKickOff(); i++) {
            executorService.submit(runnableProc);
        }
        return ResponseEntity.ok("ready!");
    }

    @GetMapping("/startex")
    public ResponseEntity<?> testMethod1() {
        ExecutorService executorService = Executors.newFixedThreadPool(5, new CustomizableThreadFactory("alpha-"+new Random().nextInt(1000)));
        for (int i = 0; i < appProp.getEndpointThreadsCountKickOff(); i++) {
            executorService.submit(runnableExtract);
        }
        return ResponseEntity.ok("ready!");
    }
}
