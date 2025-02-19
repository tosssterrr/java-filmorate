package ru.yandex.practicum.filmorate.exception;

public class LoginValidateException extends RuntimeException {
    public LoginValidateException(String message) {
        super(message);
    }
}
