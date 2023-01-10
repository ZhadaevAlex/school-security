package ru.zhadaev.schoolsecurity.api.validation;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationError {
    private Timestamp timestamp;
    private String status;
    private List<Violation> violations = new ArrayList<>();
}
