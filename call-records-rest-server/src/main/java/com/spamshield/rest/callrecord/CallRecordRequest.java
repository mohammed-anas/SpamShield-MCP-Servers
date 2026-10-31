package com.spamshield.rest.callrecord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record CallRecordRequest(
        @NotBlank String callerNumber,
        @NotBlank String calleeNumber,
        @NotNull CallDirection direction,
        @NotNull OffsetDateTime startedAt,
        @PositiveOrZero Integer durationSeconds,
        @Size(max = 64) String disposition,
        @Size(max = 4000) String notes) {}
