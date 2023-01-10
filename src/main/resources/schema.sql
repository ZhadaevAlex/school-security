create schema if not exists school;

drop table if exists school.students_courses;

drop table if exists school.students;

drop table if exists school.groups;

drop table if exists school.courses;

drop sequence if exists school.groups_group_id_sequence;

drop sequence if exists school.courses_course_id_sequence;

drop sequence if exists school.students_student_id_sequence;

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

