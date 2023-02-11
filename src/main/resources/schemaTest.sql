create schema if not exists school;

drop table if exists school.students_courses;

drop table if exists school.students;

drop table if exists school.groups;

drop table if exists school.courses;

drop table if exists school.users;

drop table if exists school.roles;

create table if not exists school.groups
(
    group_id   uuid primary key,
    group_name varchar(255) null
);

create table if not exists school.courses
(
    course_id          uuid primary key,
    course_name        varchar(255) null,
    course_description text         null
);

create table if not exists school.students
(
    student_id uuid primary key,
    group_id   uuid         null,
    first_name varchar(255) null,
    last_name  varchar(255) null,
    foreign key (group_id) references school.groups (group_id) on delete set null
);

create table if not exists school.students_courses
(
    student_id uuid null,
    course_id  uuid null,
    foreign key (student_id) references school.students (student_id) on delete set null,
    foreign key (course_id) references school.courses (course_id) on delete set null,
    unique (student_id, course_id)
);

create table if not exists school.roles
(
    role_name varchar(255) null primary key
);

create table if not exists school.users
(
    user_id       uuid primary key,
    user_username varchar(255) null,
    user_password varchar(255) null,
    role_name varchar(255) null,
    foreign key (role_name) references school.roles (role_name) on delete set null
);
