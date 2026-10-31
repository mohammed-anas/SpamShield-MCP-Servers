package com.spamshield.rest.callrecord;

import com.spamshield.common.callrecord.CallRecord;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CallRecordRepository extends ReactiveCrudRepository<CallRecord, UUID> {

    Mono<CallRecord> findByIdAndDeletedAtIsNull(UUID id);

    @Query("""
            SELECT * FROM call_record
            WHERE deleted_at IS NULL
            ORDER BY started_at DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<CallRecord> findActive(int limit, int offset);

    @Query("""
            SELECT * FROM call_record
            WHERE deleted_at IS NULL
              AND caller_number = :callerNumber
            ORDER BY started_at DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<CallRecord> findActiveByCaller(String callerNumber, int limit, int offset);
}
