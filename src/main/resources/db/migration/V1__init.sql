drop table if exists Einstellungen;
drop table if exists Messages;
drop table if exists Contacts;
drop table if exists User;


create table User(
    id int auto_increment primary key,
    name varchar(30) not null ,
    email varchar(50) not null ,
    password varchar(100) not null,
    status varchar(200),
    imgPath varchar(100)
);

create table Config(
    id int auto_increment primary key,
    ip_address varchar(32) not null,
    user int,
    foreign key(user) references User(id)
);

create table Authorities(
    id int auto_increment primary key,
    USER boolean,
    LEADER boolean,
    ADMIN boolean,
    usr int,
    foreign key(usr) references User(id)
);

create table Contacts(
    id int auto_increment primary key,
    userIn int,
    userOut int,
    foreign key(userIn) references User(id),
    foreign key(userOut) references User(id)
);

create table Messages(
    id int auto_increment primary key,
    userIn int,
    userOut int,
    foreign key(userIn) references User(id),
    foreign key(userOut) references User(id),
    message varchar(1000) not null,
    time date not null,
    unread boolean
);

create table Einstellungen(
    id int auto_increment primary key,
    user int,
    foreign key(user) references User(id),
    LIGTH_MODE boolean default 1,
    DARK_MODE boolean default 0
);
