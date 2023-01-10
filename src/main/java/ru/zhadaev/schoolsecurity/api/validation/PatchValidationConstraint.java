package ru.zhadaev.schoolsecurity.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PatchValidationConstraint implements ConstraintValidator<PatchValidation, String> {
    @Override
    public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext) {
        if (str == null) {
            return true;
        }

        return str.trim().length() > 0;
    }
}
