package com.example.elasticSearch.annotations;


import com.example.elasticSearch.annotations.validators.EnrollmentChildRelationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnrollmentChildRelationValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnrollmentChildRelation {

    String message() default "Parent relation id must not be null and name must be equal to ENROLLMENT_RELATION_NAME";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
