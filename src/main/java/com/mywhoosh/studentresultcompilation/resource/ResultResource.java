package com.mywhoosh.studentresultcompilation.resource;

import com.mywhoosh.studentresultcompilation.dto.ResultDTO;
import com.mywhoosh.studentresultcompilation.service.ResultService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("results")
@AllArgsConstructor
@RestController
@Validated
public class ResultResource {

    private ResultService resultService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<ResultDTO> getAll() {
        return resultService.getAll();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResultDTO> getById(@PathVariable("id") @NotBlank(message = "Student id can't be empty.") final String id) {
        return resultService.getById(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResultDTO> updateById(@PathVariable("id") @NotBlank(message = "Student id can't be empty.") final String id,
                                       @Valid @RequestBody final ResultDTO resultDTO) {
        return resultService.update(id, resultDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResultDTO> create(@RequestBody @Valid final ResultDTO resultDTO) {
        return resultService.save(resultDTO);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable @NotBlank(message = "Student id can't be empty.") final String id) {
        return resultService.delete(id);
    }
}
