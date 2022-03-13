package com.occ.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        name="SaleExtrDataLck",
        uniqueConstraints=
        @UniqueConstraint(name = "UniqueRowCombo",columnNames={"startRowNo", "endRowNo"})
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleExtrDataLck implements Serializable {
    public enum processing_status{ INIT,STARTED,COMPLETED};
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private Instant timeStamp;
    private Long startRowNo;
    private Long endRowNo;
    private String performedBy;
    private  processing_status status = processing_status.INIT;
    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Long version;
}
