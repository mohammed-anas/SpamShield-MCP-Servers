package com.spamshield.rest.callrecord;

import com.spamshield.common.callrecord.CallRecord;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CallRecordResponse(
        UUID id,
        String callerNumber,
        String calleeNumber,
        CallDirection direction,
        OffsetDateTime startedAt,
        Integer durationSeconds,
        String disposition,
        String notes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {

    public static CallRecordResponse from(CallRecord row) {
        return new CallRecordResponse(
                row.getId(),
                row.getCallerNumber(),
                row.getCalleeNumber(),
                CallDirection.valueOf(row.getDirection()),
                row.getStartedAt(),
                row.getDurationSeconds(),
                row.getDisposition(),
                row.getNotes(),
                row.getCreatedAt(),
                row.getUpdatedAt());
    }
}
