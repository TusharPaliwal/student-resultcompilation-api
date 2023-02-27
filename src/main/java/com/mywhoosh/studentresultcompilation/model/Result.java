package com.mywhoosh.studentresultcompilation.model;

import com.mywhoosh.studentresultcompilation.enums.RemarkEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
 * Document for storing result information to database.
 */
@Data
@Builder(toBuilder = true)
@Document(collection = "results")
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    @Min(value = 1, message = "Total marks can't be less than 1.")
    private int totalMarks;
    @Min(value = 0, message = "Obtained marks can't be less than 0.")
    private int obtainedMarks;
    @Min(value = 1, message = "Student roll number can't be less than 1.")
    @Max(value = 100, message = "Student roll number can't be more than 100.")
    private int rollNumber;
    @Min(value = 1, message = "Student grade can't be less than 1.")
    @Max(value = 10, message = "Student grade can't be more than 10.")
    private int grade;
    @NotNull
    private RemarkEnum remark;
    @Min(value = 1, message = "Position can't be less than 1.")
    @Max(value = 100, message = "Position can't be more than 100.")
    @Builder.Default
    private int positionInClass = 1;

    @CreatedDate
    private LocalDateTime createdOn;
    @LastModifiedDate
    private LocalDateTime updatedOn;

    /**
     * Determines {@link RemarkEnum} based upon percentage of student.
     *
     * <p>Student with more than equals to 50 percentage will be considered passed otherwise failed.</>
     *
     * @return remark(passed/failed) for student
     */
    public RemarkEnum getRemark() {
        int percent = (this.obtainedMarks * 100) / this.totalMarks;
        if (percent >= 50) return RemarkEnum.PASSED;
        else return RemarkEnum.FAILED;
    }

}
