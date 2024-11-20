package com.visionbagel.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.visionbagel.enums.E_COST_TYPE;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "`trade`")
public class Trade extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false)
    @Schema(required = true)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    public User user;

    @Column(length = 32, nullable = false)
    @Schema(description = "trade no", required = true)
    public String tradeNo;

    @Column(precision = 10, scale = 2, nullable = false)
    @Schema(description = "money", required = true)
    public BigDecimal money;

    @Column(nullable = false)
    @Schema(description = "pay status", required = true)
    public boolean payStatus;

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
