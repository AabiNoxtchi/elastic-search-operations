package com.example.elasticSearch.annotations;

import com.example.elasticSearch.models.Enrollment;
import com.example.elasticSearch.models.Grade;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.join.JoinField;

import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

public class ValidEnrollmentChildRelationTest {

    private final Validator validator = buildDefaultValidatorFactory().getValidator();

    @Test
    public void testValidEnrollment() {
        // create
        Enrollment validEnrollment = new Enrollment(
                "id", Grade.E, "some notes", "course_id", "course name",
                new JoinField<>(Enrollment.ENROLLMENT_RELATION_NAME, "validStudentId"));

        // test
        Set<ConstraintViolation<Enrollment>> violations = validator.validate(validEnrollment);

        // assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidEnrollment_withNullRelation() {
        // create
        Enrollment invalidEnrollmentWithNullRelation = new Enrollment(
                "id", Grade.E, "some notes", "course_id", "course name",
                null);

        // test
        Set<ConstraintViolation<Enrollment>> violations = validator.validate(invalidEnrollmentWithNullRelation);

        // assert
        assertFalse(violations.isEmpty());
        assertTrue(violations
                .stream()
                .anyMatch(violation -> "Parent relation must not be null"
                        .equals(violation.getMessage())));
        assertTrue(violations
                .stream()
                .anyMatch(violation -> "Parent relation id must not be null and name must be equal to ENROLLMENT_RELATION_NAME"
                        .equals(violation.getMessage())));
    }

    @Test
    public void testInvalidEnrollment_withNullParentId() {
        // create
        Enrollment invalidEnrollmentWithNullRelation = new Enrollment(
                "id", Grade.E, "some notes", "course_id", "course name",
                new JoinField<>(Enrollment.ENROLLMENT_RELATION_NAME, null));

        // test
        Set<ConstraintViolation<Enrollment>> violations = validator.validate(invalidEnrollmentWithNullRelation);

        // assert
        assertFalse(violations.isEmpty());
        assertTrue(violations
                .stream()
                .anyMatch(violation -> "Parent relation id must not be null and name must be equal to ENROLLMENT_RELATION_NAME"
                        .equals(violation.getMessage())));
    }

    @Test
    public void testInvalidEnrollment_withWrongRelationName() {
        // create
        Enrollment invalidEnrollmentWithNullRelation = new Enrollment(
                "id", Grade.E, "some notes", "course_id", "course name",
                new JoinField<>("wrong_name", "validStudentId"));

        // test
        Set<ConstraintViolation<Enrollment>> violations = validator.validate(invalidEnrollmentWithNullRelation);

        // assert
        assertFalse(violations.isEmpty());
        assertTrue(violations
                .stream()
                .anyMatch(violation -> "Parent relation id must not be null and name must be equal to ENROLLMENT_RELATION_NAME"
                        .equals(violation.getMessage())));
    }

}
