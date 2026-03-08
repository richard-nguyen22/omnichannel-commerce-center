--liquibase formatted sql

--changeset Tuan:202603071300
create table if not exists user_registration (
  guid uuid primary key,
  user_email varchar(255) not null,
  verification_code char(6) not null,
  verification_token varchar(255) not null,
  status varchar(20) not null default 'PENDING',
  code_expires_at timestamp with time zone not null,
  verified_at timestamp with time zone null,
  created_date timestamp with time zone not null default now(),
  updated_date timestamp with time zone not null default now()
);

create unique index if not exists uq_user_registration_verification_token
  on user_registration (verification_token);

create index if not exists idx_user_registration_email
  on user_registration (user_email);

create table if not exists user_login (
  guid uuid primary key,
  user_email varchar(255) not null,
  user_name varchar(120) not null,
  password varchar(255) not null,
  password_hash varchar(255) not null,
  status varchar(20) not null default 'ACTIVE',
  created_date timestamp with time zone not null default now(),
  updated_date timestamp with time zone not null default now()
);

create unique index if not exists uq_user_login_user_email
  on user_login (user_email);

create unique index if not exists uq_user_login_user_email_status
  on user_login (user_email, status);

--changeset Tuan:202603081300
create table if not exists user_login (
  guid uuid primary key,
  user_email varchar(255) not null,
  user_name varchar(120) not null,
  password varchar(255) not null,
  password_hash varchar(255) not null,
  status varchar(20) not null default 'ACTIVE',
  created_date timestamp with time zone not null default now(),
  updated_date timestamp with time zone not null default now()
);
