package com.mywhoosh.studentresultcompilation.resource;

import com.mywhoosh.studentresultcompilation.dto.StudentDTO;
import com.mywhoosh.studentresultcompilation.dto.ValidationErrorResponse;
import com.mywhoosh.studentresultcompilation.dto.Violation;
import com.mywhoosh.studentresultcompilation.exception.StudentNotFoundException;
import com.mywhoosh.studentresultcompilation.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = StudentResource.class, excludeAutoConfiguration = {
        ReactiveUserDetailsServiceAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class
})
public class StudentResourceTests {

    private static final String ID = "63f91c4dcf4ea943b2ab9d0d";
    private static final String NAME = "Alex";
    private static final String FATHER_NAME = "Alex father";
    private static final int ROLL_NUMBER = 4;
    private static final int GRADE = 2;

    private static final StudentDTO STUDENT_DTO =
            StudentDTO.builder().name(NAME).fatherName(FATHER_NAME).rollNumber(ROLL_NUMBER).grade(GRADE).build();

    @Value("${student.not-found.error-message}")
    private String studentNotFoundErrorMessage;


    @Autowired
    private WebTestClient webClient;

    @MockBean
    private StudentService studentService;

    @Test
    public void createStudent_success() {
        StudentDTO studentDTO = STUDENT_DTO.toBuilder().id(ID).build();

        when(studentService.save(STUDENT_DTO)).thenReturn(Mono.just(studentDTO));

        webClient
                .post().uri("/students")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(STUDENT_DTO)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(StudentDTO.class)
                .isEqualTo(studentDTO);
    }

    @Test
    public void createStudent_withInvalidFields_returnBadRequest() {
        StudentDTO studentDTO = STUDENT_DTO.toBuilder().id(ID).build();
        StudentDTO invalidFieldStudent = STUDENT_DTO.toBuilder().fatherName("").build();

        when(studentService.save(STUDENT_DTO)).thenReturn(Mono.just(studentDTO));

        webClient
                .post().uri("/students")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidFieldStudent)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void updateStudent_success() {
        String fatherName = "Updated father name";
        StudentDTO studentDTOToBeUpdated = STUDENT_DTO.toBuilder().fatherName(fatherName).build();
        StudentDTO studentDTO = STUDENT_DTO.toBuilder().id(ID).fatherName(fatherName).build();

        when(studentService.update(ID, studentDTOToBeUpdated)).thenReturn(Mono.just(studentDTO));

        webClient
                .put().uri("/students/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(studentDTOToBeUpdated)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(StudentDTO.class)
                .isEqualTo(studentDTO);
    }

    @Test
    public void getStudent_success() {
        StudentDTO studentDTO = STUDENT_DTO.toBuilder().id(ID).build();

        when(studentService.getById(ID)).thenReturn(Mono.just(studentDTO));

        webClient
                .get().uri("/students/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(StudentDTO.class)
                .isEqualTo(studentDTO);
    }

    @Test
    public void getStudent_withNonExistingStudentId_returnsNotFound() {
        String id = "test";

        when(studentService.getById(id)).thenReturn(Mono.error(new StudentNotFoundException(studentNotFoundErrorMessage)));

        webClient
                .get().uri("/students/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ValidationErrorResponse.class)
                .isEqualTo(new ValidationErrorResponse(List.of(new Violation(studentNotFoundErrorMessage))));
    }

    @Test
    public void deletedStudent_success() {

        when(studentService.delete(ID)).thenReturn(Mono.empty());

        webClient
                .delete().uri("/students/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    public void getAllStudents_success() {
        StudentDTO studentDTO = STUDENT_DTO.toBuilder().id(ID).build();

        when(studentService.getAll()).thenReturn(Flux.just(studentDTO));

        webClient
                .get().uri("/students")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(StudentDTO.class)
                .isEqualTo(List.of(studentDTO));
    }

}
