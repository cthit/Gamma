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
  user_id           uuid            primary key,
  cid               varchar(10)     not null constraint ituser_cid_unique unique,
  password          varchar(255)    not null,
  nick              varchar(50)     not null,
  first_name        varchar(50)     null,
  last_name         varchar(50)     null,
  email             varchar(100)    not null constraint ituser_email_unique unique,
  phone             varchar(15)     null,
  language          varchar(15)     null,
  avatar_url        varchar(255)    default 'default.jpg',
  gdpr              boolean         not null default false,
  user_agreement    boolean         not null default false,
  account_locked    boolean         not null default false,
  acceptance_year   integer         constraint ituser_valid_year check (acceptance_year >= 2001),
  activated         boolean         DEFAULT FALSE
);

create table ituser_website (
  id          uuid constraint ituser_website_pk primary key,
  user_id      uuid not null references ituser,
  website     uuid not null references website_url
);

/*create table ituser_gdpr (

)*/

create table password_reset_token(
  token   varchar(100) not null,
  user_id  uuid primary key references ituser
);

create table fkit_super_group (
  id            uuid                    constraint fkit_super_group_pk                  primary key,
  name          varchar(50)    not null constraint fkit_super_group_name_unique         unique,
  pretty_name   varchar(50)    not null,
  email         varchar(100)   not null,
  type          varchar(30)    not null,
  description   uuid           references internal_text
);

create table fkit_group (
  id                uuid                  constraint fkit_group_pk primary key,
  name              varchar(50)  not null constraint fkit_group_name_unique unique,
  pretty_name       varchar(50)  not null,
  becomes_active    date         not null,
  becomes_inactive  date         not null, constraint inactive_after_inactive check (becomes_active < becomes_inactive),
  fkit_super_group  uuid         not null references fkit_super_group,
  email             varchar(100) null,
  avatar_url        varchar(255) null
);

create table post (
  id        uuid constraint post_pk primary key,
  post_name uuid not null references internal_text,
  email_prefix VARCHAR(20)
);

create table authority_level (
    authority_level varchar(30) primary key
);

create table authority (
  fkit_group_id   uuid  constraint authority_fkit_super_group_fk            references fkit_super_group,
  post_id         uuid  constraint authority_post                     references post,
  authority_level varchar(30)  constraint authority_authority_level            references authority_level,
  constraint      authority_pk primary key (post_id, fkit_group_id, authority_level) --on delete cascade
);

/*create table authority_all_posts (
 fkit_group_id   uuid  constraint authority_all_posts_fkit_super_group_fk            references fkit_super_group,
 authority_level uuid  constraint authority_all_posts_authority_level                references authority_level,
 constraint      authority_all_posts_pk primary key (fkit_group_id, authority_level) on delete cascade
);
*/

create table fkit_group_website(
  id          uuid constraint fkit_group_website_pk primary key,
  fkit_group  uuid not null references fkit_group,
  website     uuid not null references website_url
);


create table membership (
  user_id            uuid         constraint membership_ituser_fk references ituser,
  fkit_group_id        uuid         constraint membership_fkit_group_fk references fkit_group,
  post_id              uuid         constraint membership_post_fk references post,
  unofficial_post_name varchar(100) null,
  constraint membership_pk primary key (user_id, fkit_group_id, post_id) --on delete cascade
);

create table no_account_membership (
    user_name            varchar(20) not null,
    fkit_group_id        uuid         constraint no_account_membership_fkit_group_fk references fkit_group,
    post_id              uuid         not null constraint no_account_membership_post_fk references post,
    unofficial_post_name varchar(100) null,
    constraint no_account_membership_pk primary key (user_name, fkit_group_id)
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
    access_token_validity integer not null,
    refresh_token_validity integer not null,
    auto_approve boolean default false not null,
    name varchar(30) not null,
    description uuid references internal_text
);

create table apikey (
    id               uuid constraint apikey_pk primary key,
    name             varchar(30) not null,
    description      uuid references internal_text,
    key              varchar(150) not null
);

create table it_user_approval (
  user_id UUID REFERENCES ituser,
  itclient_id varchar(75) REFERENCES itclient(client_id),
  CONSTRAINT it_user_approval_pk PRIMARY KEY(user_id, itclient_id)
);
