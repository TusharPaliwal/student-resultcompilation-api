package com.mywhoosh.studentresultcompilation.model;


import com.mywhoosh.studentresultcompilation.enums.StudentStatusEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Document for storing student information to database.
 */
@Data
@Builder(toBuilder = true)
@Document(collection = "students")
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    @NotBlank(message = "Student name can't be blank.")
    private String name;
    @Min(value = 1, message = "Student roll number can't be less than 1.")
    @Max(value = 100, message = "Student roll number can't be more than 100.")
    private int rollNumber;
    @NotBlank(message = "Student father's name can't be blank.")
    private String fatherName;
    @Min(value = 1, message = "Student grade can't be less than 1.")
    @Max(value = 10, message = "Student grade can't be more than 10.")
    private int grade;
    @Builder.Default
    @NotNull
    private StudentStatusEnum studentStatus = StudentStatusEnum.ACTIVE;
    @CreatedDate
    private LocalDateTime createdOn;
    @LastModifiedDate
    private LocalDateTime updatedOn;
}
