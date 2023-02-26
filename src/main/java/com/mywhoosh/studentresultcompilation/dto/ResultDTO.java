package com.mywhoosh.studentresultcompilation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Provides details of result.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultDTO {

    private String id;
    @Min(value = 1, message = "Total marks can't be less than 1.")
    @JsonProperty("total_marks")
    private int totalMarks;
    @JsonProperty("obtained_marks")
    @Min(value = 0, message = "Obtained marks can't be less than 0.")
    private int obtainedMarks;
    @Min(value = 1, message = "Student roll number can't be less than 1.")
    @Max(value = 100, message = "Student roll number can't be more than 100.")
    @JsonProperty("roll_number")
    private int rollNumber;
    @Min(value = 1, message = "Student grade can't be less than 1.")
    @Max(value = 10, message = "Student grade can't be more than 10.")
    private int grade;

}
