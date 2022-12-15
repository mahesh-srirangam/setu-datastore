/**
 * Author:  baldeep
 * Created: 28-Oct-2022
 */
CREATE TABLE IF NOT EXISTS connector (
    id varchar(255) NOT NULL,
    created timestamp NOT NULL,
    modified timestamp NOT NULL,
    created_by varchar(255) NULL,
    modified_by varchar(255) NULL,
    created_by_org varchar(255) NULL,
    modified_by_org varchar(255) NULL,
    notes varchar(2048) NULL,
    "name" varchar(255) NOT NULL,
    code varchar(100) NULL,
    "version" INT4 NOT NULL,
    status VARCHAR(128),
    shared BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk__connector_code UNIQUE ("code", "created_by_org"),
    CONSTRAINT pk_connector PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_org ON connector (created_by_org);

CREATE TABLE IF NOT EXISTS "service" (
    id varchar(255) NOT NULL,
    connector_id varchar(255) NOT NULL,
    created timestamp NOT NULL,
    modified timestamp NOT NULL,
    created_by varchar(255) NULL,
    modified_by varchar(255) NULL,
    created_by_org varchar(255) NULL,
    modified_by_org varchar(255) NULL,
    notes varchar(2048) NULL,
    "name" varchar(255) NOT NULL,
    code varchar(100) NULL,
    category VARCHAR(128),
    status varchar(128),
    config JSONB,
    flow JSONB,
    trigger VARCHAR(128),
    "version" int4 NOT NULL DEFAULT 0,
    CONSTRAINT uk_service_code UNIQUE ("code", "created_by_org"),
    CONSTRAINT pk_service PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_org ON "service" (created_by_org);

ALTER TABLE "service" ADD CONSTRAINT FK_connector_service FOREIGN KEY (connector_id) REFERENCES connector(id);