package com.spamshield.rest.callrecord;

import java.util.List;

public record CallRecordPage(List<CallRecordResponse> records) {}
