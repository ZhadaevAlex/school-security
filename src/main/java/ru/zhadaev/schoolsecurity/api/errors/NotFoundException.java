package ru.zhadaev.schoolsecurity.api.errors;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String s) {
        super(s);
    }
}
