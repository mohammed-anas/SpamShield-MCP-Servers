package com.spamshield.rest.callrecord;

import com.spamshield.common.callrecord.CallRecord;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class CallRecordService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    private final CallRecordRepository repository;

    public CallRecordService(CallRecordRepository repository) {
        this.repository = repository;
    }

    public Mono<CallRecordResponse> create(CallRecordRequest request) {
        CallRecord row = CallRecord.create(
                UUID.randomUUID(),
                request.callerNumber().trim(),
                request.calleeNumber().trim(),
                request.direction().name(),
                request.startedAt(),
                request.durationSeconds(),
                blankToNull(request.disposition()),
                blankToNull(request.notes()),
                OffsetDateTime.now(ZoneOffset.UTC));
        return repository.save(row).map(CallRecordResponse::from);
    }

    public Mono<CallRecordResponse> get(UUID id) {
        return active(id).map(CallRecordResponse::from);
    }

    public Mono<CallRecordPage> list(String callerNumber, Integer limit, Integer offset) {
        int size = bounded(limit);
        int from = offset == null || offset < 0 ? 0 : offset;
        var rows = callerNumber == null || callerNumber.isBlank()
                ? repository.findActive(size, from)
                : repository.findActiveByCaller(callerNumber.trim(), size, from);
        return rows.map(CallRecordResponse::from).collectList().map(CallRecordPage::new);
    }

    public Mono<CallRecordResponse> update(UUID id, CallRecordRequest request) {
        return active(id)
                .flatMap(row -> {
                    row.apply(
                            request.callerNumber().trim(),
                            request.calleeNumber().trim(),
                            request.direction().name(),
                            request.startedAt(),
                            request.durationSeconds(),
                            blankToNull(request.disposition()),
                            blankToNull(request.notes()),
                            OffsetDateTime.now(ZoneOffset.UTC));
                    return repository.save(row);
                })
                .map(CallRecordResponse::from);
    }

    public Mono<Void> delete(UUID id) {
        return active(id)
                .flatMap(row -> {
                    row.markDeleted(OffsetDateTime.now(ZoneOffset.UTC));
                    return repository.save(row);
                })
                .then();
    }

    private Mono<CallRecord> active(UUID id) {
        return repository
                .findByIdAndDeletedAtIsNull(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Call record not found")));
    }

    private static int bounded(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
