package com.mywhoosh.studentresultcompilation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Provides details of student.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDTO {

    private String id;
    @NotBlank(message = "Student name can't be blank.")
    private String name;
    @Min(value = 1, message = "Student roll number can't be less than 1.")
    @Max(value = 100, message = "Student roll number can't be more than 100.")
    @JsonProperty("roll_number")
    private int rollNumber;
    @NotBlank(message = "Student father's name can't be blank.")
    @JsonProperty("father_name")
    private String fatherName;
    @Min(value = 1, message = "Student grade can't be less than 1.")
    @Max(value = 10, message = "Student grade can't be more than 10.")
    private int grade;

}
