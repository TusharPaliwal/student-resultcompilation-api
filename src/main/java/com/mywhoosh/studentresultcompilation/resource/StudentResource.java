package com.mywhoosh.studentresultcompilation.resource;

import com.mywhoosh.studentresultcompilation.dto.StudentDTO;
import com.mywhoosh.studentresultcompilation.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("students")
@AllArgsConstructor
@RestController
@Validated
public class StudentResource {

    private StudentService studentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<StudentDTO> getAll() {
        return studentService.getAll();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<StudentDTO> getById(@PathVariable("id") @NotBlank(message = "Student id can't be empty.") final String id) {
        return studentService.getById(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<StudentDTO> updateById(@PathVariable("id") @NotBlank(message = "Student id can't be empty.") final String id,
                                       @Valid @RequestBody final StudentDTO student) {
        return studentService.update(id, student);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<StudentDTO> create(@RequestBody @Valid final StudentDTO student) {
        return studentService.save(student);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable @NotBlank(message = "Student id can't be empty.") final String id) {
        return studentService.delete(id);
    }
}
