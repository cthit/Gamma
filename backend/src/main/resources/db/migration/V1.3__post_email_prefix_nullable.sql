alter table post alter column email_prefix drop not null;

alter table post alter column email_prefix drop default;
