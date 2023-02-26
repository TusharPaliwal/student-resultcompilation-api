package com.mywhoosh.studentresultcompilation.repository;

import com.mywhoosh.studentresultcompilation.enums.StudentStatusEnum;
import com.mywhoosh.studentresultcompilation.model.Student;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Repository for managing student document.
 */
@Repository
public interface StudentRepository extends ReactiveMongoRepository<Student, String> {

    Flux<Student> findAllByStudentStatus(StudentStatusEnum status);
    Mono<Student> findByIdAndStudentStatus(String id, StudentStatusEnum status);

    Mono<Student> findByRollNumberAndGradeAndStudentStatus(int rollNumber, int grade, StudentStatusEnum status);

}

