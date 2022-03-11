package com.occ.booking.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class AppProp {
    @Value("${app.sa-extractor.batch.size}")
    private Integer saExtrBatchSize;
    @Value("${app.sa-extractor.data.size}")
    private Integer saExtrDataSize;

    @Value("${app.sa-processor.data.size}")
    private Integer saProcDataSize;
//    @Value("${app.sa-processor.batch.size}")
//    private Integer saProcBatchSize;

    @Value("${app.endpoint.threads.kickoff}")
    private Integer endpointThreadsCountKickOff;
}
