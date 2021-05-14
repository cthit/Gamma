create table internal_text (
  text_id  uuid constraint text_pk primary key,
  sv  text not null,
  en  text,
  version int
);

create table ituser (
  user_id           uuid            primary key,
  cid               varchar(10)     not null constraint ituser_cid_unique unique,
  password          varchar(255)    not null,
  nick              varchar(50)     not null,
  first_name        varchar(50)     null,
  last_name         varchar(50)     null,
  email             varchar(100)    not null constraint ituser_email_unique unique,
  language          varchar(15)     null,
  user_agreement    boolean         not null default false,
  acceptance_year   integer,
  activated         boolean         DEFAULT FALSE,
  version int
);

create table ituser_gdpr_training (
 user_id    uuid    primary key,
 version int
);

create table ituser_account_locked (
 user_id uuid primary key,
 version int
);

create table password_reset_token(
  token   varchar(100) not null,
  user_id  uuid primary key references ituser,
  version int
);

create table fkit_super_group (
  super_group_id            uuid           primary key,
  name          varchar(50)    not null constraint fkit_super_group_name_unique         unique,
  pretty_name   varchar(50)    not null,
  email         varchar(100)   not null,
  type          varchar(30)    not null,
  description   uuid           references internal_text,
  version int
);

create table fkit_group (
  group_id                uuid                   primary key,
  name              varchar(50)  not null constraint fkit_group_name_unique unique,
  pretty_name       varchar(50)  not null,
  super_group_id  uuid         not null references fkit_super_group,
  email             varchar(100) null,
  avatar_url        varchar(255) null,
  version int
);

create table post (
  post_id        uuid primary key,
  post_name uuid not null references internal_text,
  email_prefix VARCHAR(20),
  version int
);

create table authority_level (
    authority_level varchar(30) primary key,
    version int
);

create table authority_post (
  super_group_id   uuid  constraint authority_fkit_super_group_fk            references fkit_super_group,
  post_id         uuid  constraint authority_post                     references post,
  authority_level varchar(30)  constraint authority_authority_level            references authority_level,
  constraint      authority_pk primary key (post_id, super_group_id, authority_level),
  version int
);

create table authority_super_group (
 super_group_id   uuid  constraint authority_all_posts_super_group_fk            references fkit_super_group,
 authority_level varchar(30)  constraint authority_all_posts_authority_level                references authority_level,
 constraint      authority_all_posts_pk primary key (super_group_id, authority_level),
 version int
);

create table authority_user (
    user_id uuid references ituser,
    authority_level varchar(30),
    constraint authority_user_pk primary key (user_id, authority_level),
    version int
);

create table membership (
  user_id            uuid         constraint membership_ituser_fk references ituser,
  group_id         uuid         constraint membership_fkit_group_fk references fkit_group,
  post_id              uuid         constraint membership_post_fk references post,
  unofficial_post_name varchar(100) null,
  constraint membership_pk primary key (user_id, group_id, post_id),
  version int
);

create table whitelist_cid (
  cid varchar(10) primary key,
  constraint check_lowercase_cid check (lower(cid) = cid),
  version int
);

create table activation_code (
  cid         varchar(10)     primary key references whitelist,
  code        varchar(10)     not null,
  created_at  timestamp       not null default current_timestamp,
  version int
);

create table itclient (
    client_id varchar(75) primary key,
    client_secret varchar(75) not null,
    web_server_redirect_uri varchar(256) not null,
    auto_approve boolean default false not null,
    name varchar(30) not null,
    description uuid references internal_text,
    version int
);

create table itclient_authority_level_restriction (
    client_id varchar(75) references itclient(client_id),
    authority_level varchar(30) references authority_level(authority_level),
    constraint itclient_authority_level_restriction_pk PRIMARY KEY(client_id, authority_level)
);

create table apikey (
    api_key_id               uuid primary key,
    name             varchar(30) not null,
    description      uuid references internal_text,
    key              varchar(150) not null,
    key_type         varchar(30) not null,
    version int
);

create table itclient_apikey (
    client_id varchar(75) primary key references itclient(client_id),
    api_key_id uuid references apikey(api_key_id)
);

create table it_user_approval (
  user_id UUID REFERENCES ituser,
  client_id varchar(75) REFERENCES itclient(client_id),
  version int,
  CONSTRAINT it_user_approval_pk PRIMARY KEY(user_id, client_id)
);

create table super_group_type (
    name varchar(30) PRIMARY KEY,
    version int
);

create table user_avatar_uri (
    user_id UUID REFERENCES ituser,
    avatar_uri varchar(255),
    version int
);

create table christmas_nick (
    user_id uuid references ituser,
    nick varchar(50) not null
);