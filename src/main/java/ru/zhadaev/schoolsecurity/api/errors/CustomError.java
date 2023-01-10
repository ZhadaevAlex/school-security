package ru.zhadaev.schoolsecurity.api.errors;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CustomError {
    private final Timestamp timestamp;
    private final String status;
    private final String message;
}