# How to write migrations

Create a sql file with a V that is +1 of the last one.

Here're some examples with an sql file named `V99__website-changes.sql`:

```sql

-- Add column
ALTER TABLE website
  ADD test_column varchar(100) not null;

-- Rename column
ALTER TABLE website
  RENAME COLUMN name TO name_new;

-- Modify column
ALTER TABLE website
  ALTER COLUMN pretty_name TYPE varchar(200);

```

More examples here, check for PostgresSQL: https://www.postgresql.org/docs/9.4/ddl-alter.html
