package com.mywhoosh.studentresultcompilation.service;

import com.mywhoosh.studentresultcompilation.dto.ResultDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResultService {

    Flux<ResultDTO> getAll();
    Mono<ResultDTO> getById(final String id);
    Mono<ResultDTO> update(final String id, final ResultDTO resultDTO);
    Mono<ResultDTO> save(final ResultDTO resultDTO);
    Mono<Void> delete(final String id);

}
