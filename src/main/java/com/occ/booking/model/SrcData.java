package com.occ.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SrcData implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID uuid;
    private Instant timeStamp;
    private byte[] binary;
}
