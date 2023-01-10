package ru.zhadaev.schoolsecurity.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String s) {
        super(s);
    }
}
