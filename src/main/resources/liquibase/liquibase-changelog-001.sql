--liquibase formatted sql

--changeset Tuan:0000
create table if not exists user_login (
    guid uuid primary key,
    user_email varchar(255) not null,
    user_name varchar(255),
    password varchar(255),
    password_hash varchar(255),
    status varchar(100) not null default 'ACTIVE',
    created_by_user_login_guid uuid not null default '00000000-0000-0000-0000-000000000000',
    updated_by_user_login_guid uuid not null default '00000000-0000-0000-0000-000000000000',
    created_date timestamp with time zone not null default now(),
    updated_date timestamp with time zone not null default now()
    );

insert into user_login(guid, user_email, user_name, password, password_hash, status)
values ('00000000-0000-0000-0000-000000000000', 'system@local', 'System Default User', 'IAMSuperUser#1', 'IAMSuperUser#1', 'ACTIVE')
    on conflict (guid) do nothing;

create table if not exists client_tenant_hdr (
    guid uuid primary key,
    code varchar(80) not null,
    name varchar(160) not null,
    jdbc_url varchar(500) not null,
    db_username varchar(120) not null,
    db_password varchar(255) not null,
    status varchar(20) not null default 'ACTIVE',
    created_date timestamp with time zone not null default now(),
    updated_date timestamp with time zone not null default now()
    );
COMMENT ON COLUMN client_tenant_hdr.jdbc_url IS
'Format: jdbc:postgresql://<host>:<port>/<database>.
Use this query to get values:
SELECT current_database() AS database,
       current_user AS user,
       inet_server_addr() AS host,
       inet_server_port() AS port;';
create unique index if not exists uq_client_tenant_hdr_code
    on client_tenant_hdr (code);
create index if not exists idx_client_tenant_hdr_status
    on client_tenant_hdr (status);

--changeset Tuan:0001-create-user_registration
create table if not exists user_registration (
    guid uuid primary key,
    user_email varchar(255) not null,
    verification_code char(6),
    verification_token varchar(255),
    verification_status varchar(20) not null default 'PENDING',
    verification_remarks text,
    status varchar(20) not null default 'ACTIVE',
    code_expires_at timestamp with time zone not null,
    verified_at timestamp with time zone null,
    created_by_user_login_guid uuid not null default '00000000-0000-0000-0000-000000000000',
    updated_by_user_login_guid uuid not null default '00000000-0000-0000-0000-000000000000',
    created_date timestamp with time zone not null default now(),
    updated_date timestamp with time zone not null default now()
);

create unique index if not exists uq_user_registration_verification_token
    on user_registration (verification_token);

create index if not exists idx_user_registration_email
    on user_registration (user_email);

create index if not exists idx_user_registration_email_code
    on user_registration (user_email, verification_code);

--changeset codex:002-create-auth_refresh_token
create table if not exists auth_refresh_token (
    id uuid primary key,
    user_guid uuid not null,
    token_hash varchar(64) not null,
    expires_at timestamp with time zone not null,
    revoked_at timestamp with time zone null,
    created_at timestamp with time zone not null default now(),
    constraint fk_auth_refresh_token_user
        foreign key (user_guid) references user_login(guid) on delete cascade
);

create unique index if not exists uq_auth_refresh_token_hash
    on auth_refresh_token (token_hash);

create index if not exists idx_auth_refresh_token_user_guid
    on auth_refresh_token (user_guid);

create index if not exists idx_auth_refresh_token_expires_at
    on auth_refresh_token (expires_at);

--changeset Tuan:0003-create-permission_hdr
create table if not exists permission_hdr (
    guid uuid primary key,
    code varchar(100) not null,
    name varchar(120) not null,
    created_date timestamp with time zone not null default now(),
    updated_date timestamp with time zone not null default now()
);

create unique index if not exists uq_permission_hdr_code
    on permission_hdr (code);

--changeset Tuan:0004-create-user_permission_link
create table if not exists user_permission_link (
    user_guid uuid not null,
    permission_guid uuid not null,
    created_date timestamp with time zone not null default now(),
    constraint pk_user_permission_link primary key (user_guid, permission_guid),
    constraint fk_user_permission_link_user
        foreign key (user_guid) references user_login(guid) on delete cascade,
    constraint fk_user_permission_link_permission
        foreign key (permission_guid) references permission_hdr(guid) on delete cascade
);
