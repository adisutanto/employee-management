create table employees (
    id varchar(10) primary key,
    login varchar(20) not null,
    name varchar(50),
    salary decimal(18,2),
    unique (login)
);