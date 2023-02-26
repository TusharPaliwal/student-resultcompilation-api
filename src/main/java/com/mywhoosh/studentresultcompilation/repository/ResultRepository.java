package com.mywhoosh.studentresultcompilation.repository;

import com.mywhoosh.studentresultcompilation.model.Result;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repository for managing result document.
 */
@Repository
public interface ResultRepository extends ReactiveMongoRepository<Result, String> {

    Flux<Result> findAllByGrade(int grade);

}
