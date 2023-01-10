package ru.zhadaev.schoolsecurity.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PatchValidationConstraint.class)
public @interface PatchValidation {
    String message() default "The string should not be empty or consist only of spaces. Can be null.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
