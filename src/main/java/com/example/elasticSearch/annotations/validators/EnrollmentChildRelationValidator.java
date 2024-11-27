package com.example.elasticSearch.annotations.validators;

import com.example.elasticSearch.annotations.ValidEnrollmentChildRelation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.elasticsearch.core.join.JoinField;

import static com.example.elasticSearch.models.Enrollment.ENROLLMENT_RELATION_NAME;

public class EnrollmentChildRelationValidator implements
        ConstraintValidator<ValidEnrollmentChildRelation, JoinField<String>> {

    @Override
    public boolean isValid(JoinField<String> joinField, ConstraintValidatorContext constraintValidatorContext) {
        if ( joinField == null || joinField.getParent() == null) {
            return false; // Parent is null, invalid
        }

        return ENROLLMENT_RELATION_NAME.equals(joinField.getName()); // Ensure it matches the constant
    }
}
