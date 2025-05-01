create table users (
  id serial not null,
  nickname varchar(128) not null,
  email varchar(128) not null unique,
  password varchar(512) not null,
  primary key(id)
);