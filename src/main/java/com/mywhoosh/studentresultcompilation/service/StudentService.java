package com.mywhoosh.studentresultcompilation.service;

import com.mywhoosh.studentresultcompilation.dto.StudentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Provides methods for managing student.
 */
public interface StudentService {

    Flux<StudentDTO> getAll();
    Mono<StudentDTO> getById(final String id);
    Mono<StudentDTO> update(final String id, final StudentDTO studentDTO);
    Mono<StudentDTO> save(final StudentDTO studentDTO);
    Mono<Void> delete(final String id);
}
