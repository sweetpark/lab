package com.example.playground.validation.source.version_2;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValidate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return value != null && value.matches(regex);
    }
}
