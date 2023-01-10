package ru.zhadaev.schoolsecurity.dao.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class School {
    private List<Group> groups = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
}
