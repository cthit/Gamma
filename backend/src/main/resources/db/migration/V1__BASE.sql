create table internal_text (
  text_id  uuid constraint text_pk primary key,
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
  user_id           uuid            primary key,
  cid               varchar(10)     not null constraint ituser_cid_unique unique,
  password          varchar(255)    not null,
  nick              varchar(50)     not null,
  first_name        varchar(50)     null,
  last_name         varchar(50)     null,
  email             varchar(100)    not null constraint ituser_email_unique unique,
  language          varchar(15)     null,
  avatar_url        varchar(255)    default 'default.jpg',
  user_agreement    boolean         not null default false,
  acceptance_year   integer,
  activated         boolean         DEFAULT FALSE
);

create table ituser_website (
  id          uuid constraint ituser_website_pk primary key,
  user_id      uuid not null references ituser,
  website     uuid not null references website_url
);

create table ituser_gdpr_training (
 user_id    uuid    primary key
);

create table ituser_account_locked (
 user_id uuid primary key
);

create table password_reset_token(
  token   varchar(100) not null,
  user_id  uuid primary key references ituser
);

create table fkit_super_group (
  super_group_id            uuid           primary key,
  name          varchar(50)    not null constraint fkit_super_group_name_unique         unique,
  pretty_name   varchar(50)    not null,
  email         varchar(100)   not null,
  type          varchar(30)    not null,
  description   uuid           references internal_text
);

create table fkit_group (
  group_id                uuid                   primary key,
  name              varchar(50)  not null constraint fkit_group_name_unique unique,
  pretty_name       varchar(50)  not null,
  becomes_active    date         not null,
  becomes_inactive  date         not null, constraint inactive_after_inactive check (becomes_active < becomes_inactive),
  super_group_id  uuid         not null references fkit_super_group,
  email             varchar(100) null,
  avatar_url        varchar(255) null
);

create table post (
  post_id        uuid primary key,
  post_name uuid not null references internal_text,
  email_prefix VARCHAR(20)
);

create table authority_level (
    authority_level varchar(30) primary key
);

create table authority (
  super_group_id   uuid  constraint authority_fkit_super_group_fk            references fkit_super_group,
  post_id         uuid  constraint authority_post                     references post,
  authority_level varchar(30)  constraint authority_authority_level            references authority_level,
  constraint      authority_pk primary key (post_id, super_group_id, authority_level)
);

/*create table authority_all_posts (
 super_group_id   uuid  constraint authority_all_posts_fkit_super_group_fk            references fkit_super_group,
 authority_level uuid  constraint authority_all_posts_authority_level                references authority_level,
 constraint      authority_all_posts_pk primary key (fkit_group_id, authority_level) on delete cascade
);
*/

create table fkit_group_website(
  id          uuid constraint fkit_group_website_pk primary key,
  group_id  uuid not null references fkit_group,
  website     uuid not null references website_url
);


create table membership (
  user_id            uuid         constraint membership_ituser_fk references ituser,
  group_id         uuid         constraint membership_fkit_group_fk references fkit_group,
  post_id              uuid         constraint membership_post_fk references post,
  unofficial_post_name varchar(100) null,
  constraint membership_pk primary key (user_id, group_id, post_id)
);

create table no_account_membership (
    user_name            varchar(20) not null,
    group_id        uuid         constraint no_account_membership_fkit_group_fk references fkit_group,
    post_id              uuid         not null constraint no_account_membership_post_fk references post,
    unofficial_post_name varchar(100) null,
    constraint no_account_membership_pk primary key (user_name, group_id)
);

create table whitelist (
  cid varchar(10) primary key,
  constraint check_lowercase_cid check (lower(cid) = cid)
);

create table activation_code (
  cid         varchar(10)     primary key references whitelist,
  code        varchar(10)     not null,
  created_at  timestamp       not null default current_timestamp
);

create table itclient (
    client_id varchar(75) primary key,
    client_secret varchar(75) not null,
    web_server_redirect_uri varchar(256) not null,
    auto_approve boolean default false not null,
    name varchar(30) not null,
    description uuid references internal_text
);

create table apikey (
    api_key_id               uuid primary key,
    name             varchar(30) not null,
    description      uuid references internal_text,
    key              varchar(150) not null,
    key_type         varchar(30) not null
);

create table it_user_approval (
  user_id UUID REFERENCES ituser,
  client_id varchar(75) REFERENCES itclient(client_id),
  CONSTRAINT it_user_approval_pk PRIMARY KEY(user_id, client_id)
);
