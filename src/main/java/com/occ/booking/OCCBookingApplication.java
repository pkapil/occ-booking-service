package com.occ.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@Slf4j
public class OCCBookingApplication implements CommandLineRunner {

    @Autowired
    BookDataRepository bookDataRepository;

    public static void main(String[] args) {
        SpringApplication.run(OCCBookingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i < 20; i++)
            bookDataRepository.save(BookData.builder()
                    .jobName("job" + i)
                    .status(BookData.processing_status.INIT)
                    .timeStamp(Instant.now())
                    .build());

    }


    @RestController
    class BookingController {

        @Autowired
        BookingService bookingService;

        Runnable runnable = () -> {
            UUID uuid = UUID.randomUUID();
            try {
                Thread.sleep(new Random().nextInt(10));
                bookingService.performJob(uuid);
            } catch (Exception e) {
                log.error("{} failed.", Thread.currentThread().getName());
            }
        };

        @GetMapping("/start")
        public ResponseEntity<?> testMethod() {
            ExecutorService executorService = Executors.newFixedThreadPool(5, new CustomizableThreadFactory("alpha-"));
            for (int i = 0; i < 100; i++) {
                executorService.submit(runnable);
            }
            return ResponseEntity.ok("ready!");
        }
    }
}
