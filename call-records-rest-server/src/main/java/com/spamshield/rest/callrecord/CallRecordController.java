package com.spamshield.rest.callrecord;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/call-records")
public class CallRecordController {

    private final CallRecordService service;

    public CallRecordController(CallRecordService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<CallRecordResponse>> create(@Valid @RequestBody CallRecordRequest request) {
        return service.create(request)
                .map(body -> ResponseEntity.created(URI.create("/api/call-records/" + body.id()))
                        .body(body));
    }

    @GetMapping("/{id}")
    public Mono<CallRecordResponse> get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public Mono<CallRecordPage> list(
            @RequestParam(required = false) String callerNumber,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        return service.list(callerNumber, limit, offset);
    }

    @PutMapping("/{id}")
    public Mono<CallRecordResponse> update(@PathVariable UUID id, @Valid @RequestBody CallRecordRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable UUID id) {
        return service.delete(id).thenReturn(ResponseEntity.noContent().build());
    }
}
