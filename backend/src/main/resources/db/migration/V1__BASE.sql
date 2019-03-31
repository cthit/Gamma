create table internal_text (
  id  uuid constraint text_pk primary key,
  sv  text not null,
  en  text
);

create table website (
  id uuid     constraint websites_pk primary key,
  name        varchar(100) not null constraint website_name unique,
  pretty_name varchar(100) not null
);

create table website_url (
  id      uuid          constraint websites_url_pk primary key,
  website uuid          not null references website,
  url     varchar(2000) not null
);

create table ituser (
  id               uuid constraint ituser_pk primary key,
  cid              varchar(10)  not null constraint ituser_cid_unique unique,
  password         varchar(255) not null,
  nick             varchar(50)  not null,
  first_name       varchar(50)  null,
  last_name        varchar(50)  null,
  email            varchar(100) not null constraint ituser_email_unique unique,
  phone            varchar(15)  null,
  language         varchar(15)  null,
  avatar_url       varchar(255) null,
  gdpr             boolean      not null default false,
  user_agreement   boolean      not null default false,
  account_locked   boolean      not null default false,
  acceptance_year  integer constraint ituser_valid_year check (acceptance_year >= 2001),
  created_at       timestamp    not null default current_timestamp,
  last_modified_at timestamp    not null default current_timestamp
);

create table ituser_website (
  id          uuid constraint ituser_website_pk primary key,
  ituser      uuid not null references ituser,
  website     uuid not null references website_url
);

create table authority_level (
  id  uuid constraint authority_level_pk primary key,
  authority_level varchar(30)
);

create table password_reset_token(
  id      uuid constraint password_reset_token_pk primary key,
  token   varchar(100) not null,
  ituser  uuid references ituser
);

create table fkit_super_group (
  id            uuid                    constraint fkit_super_group_pk                  primary key,
  name          varchar(50)    not null constraint fkit_super_group_name_unique         unique,
  pretty_name   varchar(50)    not null constraint fkit_super_group_pretty_name_unique  unique,
  type          varchar(30)    not null
);

create table fkit_group (
  id                uuid                  constraint fkit_group_pk primary key,
  name              varchar(50)  not null constraint fkit_group_name_unique unique,
  pretty_name       varchar(50)  not null constraint fkit_group_pretty_name_unique unique,
  description       uuid         null     references internal_text,
  function          uuid         not null references internal_text,
  email             varchar(100) not null constraint fkit_group_email_unique unique,
  becomes_active    date         not null,
  becomes_inactive  date         not null, constraint inactive_after_inactive check (becomes_active < becomes_inactive),
  avatar_url        varchar(255) null,
  internal_year     integer      not null constraint fkit_group_year check (internal_year >= 2001)
);

create table fkit_group_to_super_group (
  fkit_super_group_id   uuid     not null references fkit_super_group,
  fkit_group_id         uuid     not null references fkit_group,
  constraint            fkit_group_to_super_group_pk  primary key (fkit_super_group_id, fkit_group_id)
);

create table post (
  id        uuid constraint post_pk primary key,
  post_name uuid not null references internal_text
);

create table authority (
  id              uuid  constraint authority_unique unique,
  fkit_group_id   uuid  constraint authority_fkit_super_group_fk            references fkit_super_group,
  post_id         uuid  constraint authority_post                     references post,
  authority_level uuid  constraint authority_authority_level          references authority_level,
  constraint      authority_pk primary key (post_id, fkit_group_id)
);

create table fkit_group_website(
  id          uuid constraint fkit_group_website_pk primary key,
  fkit_group  uuid not null references fkit_group,
  website     uuid not null references website_url
);



create table membership (   -- Should this be rebuilt to look like all other tables? probably
  ituser_id            uuid         constraint membership_ituser_fk references ituser,
  fkit_group_id        uuid         constraint membership_fkit_group_fk references fkit_group,
  post_id              uuid         not null constraint membership_post_fk references post,
  unofficial_post_name varchar(100) null,
  constraint membership_pk primary key (ituser_id, fkit_group_id, post_id)
);

create table no_account_membership (
  ituser_id               varchar(30)  not null,
  fkit_group_id        uuid         constraint membership_fkit_group_fk references fkit_group,
  post_id              uuid         not null constraint membership_post_fk references post,
  unofficial_post_name varchar(100) null,
  constraint no_account_membership_pk primary key (ituser_id, fkit_group_id)
);

create table whitelist (
  id  uuid constraint whitelist_pk primary key,
  cid varchar(10) not null constraint whitelist_cid_unique unique
);

create table activation_code (
  id          uuid constraint activation_code_pk primary key,
  cid         uuid unique     not null references whitelist,
  code        varchar(30)     not null,
  created_at  timestamp       not null default current_timestamp
);

create table itclient (
    id  uuid constraint itclient_pk primary key,
    client_id varchar(256) not null,
    client_secret varchar(256) not null,
    web_server_redirect_uri varchar(256) not null,
    --authorities varchar(256) not null,
    access_token_validity integer not null,
    refresh_token_validity integer not null,
    auto_approve boolean default false not null,
    name varchar(30) not null,
    description uuid not null references internal_text,
    created_at       timestamp    not null default current_timestamp,
    last_modified_at timestamp    not null default current_timestamp
)