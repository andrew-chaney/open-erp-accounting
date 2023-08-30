CREATE TABLE ledger
(
    ledger_id          UUID           NOT NULL,
    title              VARCHAR(255)   NOT NULL,
    type               VARCHAR(20)    NOT NULL,
    associated_company VARCHAR(255)   NOT NULL,
    amount             DECIMAL(19, 4) NOT NULL,
    notes              VARCHAR(255),
    created_ts_epoch   TIMESTAMP      NOT NULL,
    updated_ts_epoch   TIMESTAMP      NOT NULL,
    PRIMARY KEY (ledger_id)
);

CREATE TABLE tag
(
    tag_id UUID         NOT NULL,
    name   VARCHAR(255) NOT NULL,
    PRIMARY KEY (tag_id)
);

CREATE TABLE ledger_tag
(
    ledger_id UUID REFERENCES ledger
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    tag_id    UUID REFERENCES tag
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT ledger_tag_pkey PRIMARY KEY (ledger_id, tag_id)
);
