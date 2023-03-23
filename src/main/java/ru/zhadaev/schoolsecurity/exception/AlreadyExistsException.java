package ru.zhadaev.schoolsecurity.exception;

public class AlreadyExistsException extends RuntimeException{
    public AlreadyExistsException(String s) {
        super(s);
    }
}
