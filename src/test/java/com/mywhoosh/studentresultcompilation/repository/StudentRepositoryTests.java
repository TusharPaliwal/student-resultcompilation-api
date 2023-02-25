package com.mywhoosh.studentresultcompilation.repository;

import com.mywhoosh.studentresultcompilation.enums.StudentStatusEnum;
import com.mywhoosh.studentresultcompilation.model.Student;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataMongoTest
public class StudentRepositoryTests {

    private static final String NAME = "John";
    private static final String NAME_1 = "Alex";
    private static final String NAME_2 = "Larry";
    private static final String FATHER_NAME = "John father";
    private static final String FATHER_NAME_1 = "Alex father";
    private static final String FATHER_NAME_2 = "Larry father";

    private static final int ROLL_NUMBER = 3;
    private static final int ROLL_NUMBER_1 = 4;
    private static final int ROLL_NUMBER_2 = 5;
    private static final int GRADE = 1;
    private static final int GRADE_1 = 2;
    private static final int GRADE_2 = 3;


    private static final Student STUDENT_TO_BE_PERSISTED =
            Student.builder().name(NAME).fatherName(FATHER_NAME).rollNumber(ROLL_NUMBER).grade(GRADE).build();
    private static final Student STUDENT_1 =
            Student.builder().name(NAME_1).fatherName(FATHER_NAME_1).rollNumber(ROLL_NUMBER_1).grade(GRADE_1).build();
    private static final Student STUDENT_2 =
            Student.builder().name(NAME_2).fatherName(FATHER_NAME_2).rollNumber(ROLL_NUMBER_2).grade(GRADE_2).build();
    @Autowired
    StudentRepository studentRepository;

    @Test
    public void saveStudent_success() {
        Mono<Student> studentMono = studentRepository.save(STUDENT_TO_BE_PERSISTED);

        StepVerifier
                .create(studentMono)
                .expectNext(STUDENT_TO_BE_PERSISTED)
                .verifyComplete();
    }

    @Test
    public void saveStudent_withInvalidFields_throwsValidationError() {
        Mono<Student> studentMono =
                studentRepository.save(STUDENT_TO_BE_PERSISTED.toBuilder().grade(50).name("").build());

        StepVerifier
                .create(studentMono)
                .expectError(ConstraintViolationException.class);
    }

    @Test
    public void findAllByStudentStatus_success() {
        Student deletedStudent = STUDENT_1.toBuilder().studentStatus(StudentStatusEnum.DELETED).build();

        studentRepository.deleteAll().block();
        studentRepository.save(STUDENT_1).block();
        studentRepository.save(STUDENT_2).block();
        studentRepository.save(deletedStudent).block();

        Mono<List<Student>> studentsMono =
                studentRepository.findAllByStudentStatus(StudentStatusEnum.ACTIVE).collectList();

        StepVerifier
                .create(studentsMono)
                .assertNext(students ->
                        assertThat(List.of(STUDENT_1, STUDENT_2)).usingRecursiveComparison().ignoringFields("id").isEqualTo(students)
                )
                .verifyComplete();
    }


    @Test
    public void findByIdAndStudentStatus_success() {
        studentRepository.deleteAll().block();
        Student persistedStudent = studentRepository.save(STUDENT_1).block();

        Mono<Student> studentMono =
                studentRepository.findByIdAndStudentStatus(persistedStudent.getId(), StudentStatusEnum.ACTIVE);

        StepVerifier
                .create(studentMono)
                .assertNext(student ->
                        assertThat(STUDENT_1).usingRecursiveComparison().ignoringFields("id").isEqualTo(student)
                )
                .verifyComplete();
    }


}
