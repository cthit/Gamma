alter table authority drop constraint authority_pk;
alter table authority drop constraint authority_unique;
alter table authority add constraint authority_pk primary key (id);
