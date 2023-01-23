create schema if not exists school;

drop table if exists school.students_courses;

drop table if exists school.students;

drop table if exists school.groups;

drop table if exists school.courses;

drop sequence if exists school.groups_group_id_sequence;

drop sequence if exists school.courses_course_id_sequence;

drop sequence if exists school.students_student_id_sequence;

drop table if exists school.users_roles;

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
    course_description text null
);

create table if not exists school.students
(
    student_id uuid primary key,
    group_id   uuid null,
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

create table if not exists school.users
(
    user_id         uuid primary key,
    user_username   varchar(255) null,
    user_password   varchar(255) null
);

create table if not exists school.roles
(
    role_id     uuid primary key,
    role_name   varchar(255) null
);

create table if not exists school.users_roles
(
    user_id     uuid null,
    role_id     uuid null,
    foreign key (user_id)   references school.users (user_id) on delete set null,
    foreign key (role_id)   references school.roles (role_id) on delete set null,
    unique (user_id, role_id)
);