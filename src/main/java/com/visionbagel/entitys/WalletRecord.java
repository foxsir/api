package com.visionbagel.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.visionbagel.enums.E_COST_TYPE;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "`wallet_record`")
public class WalletRecord extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false)
    @Schema(required = true)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    public User user;

    /**
     * (size/1024/1024*2/10) = 0.05411663055419922
     */
    @Schema(description = "The size of the data generated", required = true)
    public Integer dataSize;

    @Schema(description = "cost", required = true)
    public Integer cost;

    @Schema(description = "cost type", required = true)
    public E_COST_TYPE costType;

    @CreationTimestamp
    @Schema(description = "when created", required = true)
    public Instant whenCreated;

    @UpdateTimestamp
    @Schema(description = "when modified", required = true)
    public Instant whenModified;

    @SoftDelete
    @Column(nullable = true)
    public Instant whenDeleted;
}
