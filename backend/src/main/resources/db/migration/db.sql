create table ituser (
  id               uuid constraint ituser_pk primary key,
  cid              varchar(10)  not null constraint ituser_cid_unique unique,
  password         varchar(255) not null,
  nick             varchar(50)  not null,
  first_name       varchar(50)  null,
  last_name        varchar(50)  null,
  email            varchar(100) not null constraint ituser_email_unique unique,
  phone            varchar(15)  null,
  language         varchar(15)   null,
  avatar_url       varchar(255) null,
  gdpr             boolean      not null default false,
  user_agreement   boolean      not null default false,
  account_locked   boolean      not null default false,
  acceptance_year  integer constraint ituser_valid_year check (acceptance_year >= 2001),
  created_at       timestamp    not null default current_timestamp,
  last_modified_at timestamp    not null default current_timestamp
);

create table authorites (
  authority varchar(50) constraint authorites_pk primary key
);

create table fkit_group (
  id          uuid constraint fkit_group_pk primary key,
  name        varchar(50)  not null constraint fkit_group_name_unique unique,
  description text         null,
  email       varchar(100) not null constraint fkit_group_email_unique unique,
  type        varchar(30)  not null
);

create table fkit_group_authorites (
  fkit_group_id uuid,
  authorites_id varchar(50),
  constraint fkit_group_authorites._pk primary key (fkit_group_id, authorites_id)
);

create table post (
  id        uuid constraint post_pk primary key,
  post_name varchar(50) constraint post_name_unique unique
  -- post_name borde nog kanske vara post_function (som i funktionen rollen fyller) för att kunna hantera översättningar på ett någorlunda vettigt sätt
);

create table membership (
  ituser_id            uuid constraint membership_ituser_fk references ituser,
  fkit_group_id        uuid constraint membership_fkit_group_fk references fkit_group,
  post_id              uuid         not null constraint membership_post_fk references post,
  year                 integer      not null constraint membership_valid_year check (year >= 2001),
  unofficial_post_name varchar(100) null,
  constraint membership_pk primary key (ituser_id, fkit_group_id)
);

create table whitelist (
  id  uuid constraint whitelist_pk primary key,
  cid varchar(10) not null constraint whitelist_cid_unique unique
);
