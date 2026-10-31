-- Postgres runs outside this project. Apply this file in that database.
-- call_record is written by call-records-rest-server. MCP servers only read.

CREATE TABLE IF NOT EXISTS call_record (
    id UUID PRIMARY KEY,
    caller_number TEXT NOT NULL,
    callee_number TEXT NOT NULL,
    direction TEXT NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    duration_seconds INTEGER,
    disposition TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_call_record_caller
    ON call_record (caller_number, started_at DESC);

CREATE INDEX IF NOT EXISTS idx_call_record_active
    ON call_record (started_at DESC)
    WHERE deleted_at IS NULL;

CREATE TABLE IF NOT EXISTS content_analysis (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id TEXT NOT NULL,
    subject TEXT,
    body_excerpt TEXT,
    label TEXT NOT NULL,
    rationale TEXT,
    model_provider TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_content_analysis_message_id
    ON content_analysis (message_id, created_at DESC);

CREATE TABLE IF NOT EXISTS url_reputation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    url TEXT NOT NULL,
    host TEXT,
    verdict TEXT NOT NULL,
    rationale TEXT,
    model_provider TEXT,
    checked_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_url_reputation_url
    ON url_reputation (url, checked_at DESC);

CREATE TABLE IF NOT EXISTS sender_reputation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_address TEXT NOT NULL,
    domain TEXT,
    verdict TEXT NOT NULL,
    rationale TEXT,
    model_provider TEXT,
    checked_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_sender_reputation_address
    ON sender_reputation (sender_address, checked_at DESC);

CREATE TABLE IF NOT EXISTS policy_decision (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id TEXT NOT NULL,
    content_label TEXT,
    url_verdict TEXT,
    sender_verdict TEXT,
    action TEXT NOT NULL,
    rationale TEXT,
    model_provider TEXT,
    decided_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_policy_decision_message_id
    ON policy_decision (message_id, decided_at DESC);
