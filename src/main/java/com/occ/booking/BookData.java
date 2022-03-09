package com.occ.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookData implements Serializable {
    public enum processing_status{ INIT,STARTED,COMPLETED};
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Instant timeStamp;
    private String performedByThreadName;
    private String jobName;
    private  processing_status status = processing_status.INIT;
    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Long version;
}
