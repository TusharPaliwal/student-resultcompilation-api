package com.mywhoosh.studentresultcompilation.exception;

import com.mywhoosh.studentresultcompilation.dto.ValidationErrorResponse;
import com.mywhoosh.studentresultcompilation.dto.Violation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Performs exception handling for below exceptions which would be raised by any controller, if not handled in
 * controller already.
 */
@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    /**
     * Handles exceptions failure which would be raised if any applied validation for request payload fails.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse handlesConstraintValidationException(ConstraintViolationException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            error.getViolations().add(new Violation(violation.getMessage()));
        }
        return error;
    }

    /**
     * Handles exceptions failure when student is not found in system.
     */
    @ExceptionHandler(StudentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    ValidationErrorResponse handlesStudentNotFoundException(StudentNotFoundException e) {
        return new ValidationErrorResponse(List.of(new Violation(e.getMessage())));
    }

    /**
     * Handles exception failure, if supplied method argument is invalid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse handlesMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolations().add(new Violation(fieldError.getDefaultMessage()));
        }
        return error;
    }

}
