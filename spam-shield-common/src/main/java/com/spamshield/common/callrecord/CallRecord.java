package com.spamshield.common.callrecord;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("call_record")
public class CallRecord implements Persistable<UUID> {

    @Id
    private final UUID id;

    @Column("caller_number")
    private String callerNumber;

    @Column("callee_number")
    private String calleeNumber;

    private String direction;

    @Column("started_at")
    private OffsetDateTime startedAt;

    @Column("duration_seconds")
    private Integer durationSeconds;

    private String disposition;

    private String notes;

    @Column("created_at")
    private final OffsetDateTime createdAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;

    @Column("deleted_at")
    private OffsetDateTime deletedAt;

    @Transient
    private boolean isNew;

    @PersistenceCreator
    public CallRecord(
            UUID id,
            String callerNumber,
            String calleeNumber,
            String direction,
            OffsetDateTime startedAt,
            Integer durationSeconds,
            String disposition,
            String notes,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            OffsetDateTime deletedAt) {
        this.id = id;
        this.callerNumber = callerNumber;
        this.calleeNumber = calleeNumber;
        this.direction = direction;
        this.startedAt = startedAt;
        this.durationSeconds = durationSeconds;
        this.disposition = disposition;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.isNew = false;
    }

    public static CallRecord create(
            UUID id,
            String callerNumber,
            String calleeNumber,
            String direction,
            OffsetDateTime startedAt,
            Integer durationSeconds,
            String disposition,
            String notes,
            OffsetDateTime now) {
        CallRecord row = new CallRecord(
                id, callerNumber, calleeNumber, direction, startedAt, durationSeconds, disposition, notes, now, now, null);
        row.isNew = true;
        return row;
    }

    public void apply(
            String callerNumber,
            String calleeNumber,
            String direction,
            OffsetDateTime startedAt,
            Integer durationSeconds,
            String disposition,
            String notes,
            OffsetDateTime updatedAt) {
        this.callerNumber = callerNumber;
        this.calleeNumber = calleeNumber;
        this.direction = direction;
        this.startedAt = startedAt;
        this.durationSeconds = durationSeconds;
        this.disposition = disposition;
        this.notes = notes;
        this.updatedAt = updatedAt;
    }

    public void markDeleted(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
        this.updatedAt = deletedAt;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public String getCallerNumber() {
        return callerNumber;
    }

    public String getCalleeNumber() {
        return calleeNumber;
    }

    public String getDirection() {
        return direction;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public String getDisposition() {
        return disposition;
    }

    public String getNotes() {
        return notes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }
}
