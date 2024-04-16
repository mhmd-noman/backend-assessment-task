create table accounts (created_at timestamp(6) with time zone,
updated_at timestamp(6) with time zone,
id uuid not null, email varchar(255),
first_name varchar(255),
last_name varchar(255),
provider_id varchar(255),
provider_type varchar(255) check (provider_type in ('STRIPE')),
primary key (id));