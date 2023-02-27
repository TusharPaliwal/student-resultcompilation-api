package com.mywhoosh.studentresultcompilation.resource;

import com.mywhoosh.studentresultcompilation.dto.ResultDTO;
import com.mywhoosh.studentresultcompilation.dto.ValidationErrorResponse;
import com.mywhoosh.studentresultcompilation.dto.Violation;
import com.mywhoosh.studentresultcompilation.exception.ResultRecordNotFoundException;
import com.mywhoosh.studentresultcompilation.service.ResultService;
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
@WebFluxTest(controllers = ResultResource.class, excludeAutoConfiguration = {
        ReactiveUserDetailsServiceAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class
})
public class ResultResourceTests {

    private static final String ID = "63f91c4dcf4ea943b2ab9d0d";
    private static final int OBTAINED_MARKS = 60;
    private static final int TOTAL_MARKS = 100;
    private static final int ROLL_NUMBER = 3;
    private static final int GRADE = 1;

    private static final ResultDTO RESULT_DTO =
            ResultDTO.builder().obtainedMarks(OBTAINED_MARKS).totalMarks(TOTAL_MARKS).rollNumber(ROLL_NUMBER).grade(GRADE).build();

    @Value("${result.not-found.error-message}")
    private String resultRecordNotFoundErrorMessage;


    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ResultService resultService;

    @Test
    public void createResult_success() {
        ResultDTO resultDTO = RESULT_DTO.toBuilder().id(ID).build();

        when(resultService.save(RESULT_DTO)).thenReturn(Mono.just(resultDTO));

        webClient
                .post().uri("/results")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(RESULT_DTO)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ResultDTO.class)
                .isEqualTo(resultDTO);
    }

    @Test
    public void createResult_withInvalidFields_returnBadRequest() {
        ResultDTO resultDTO = RESULT_DTO.toBuilder().id(ID).build();
        ResultDTO invalidFieldResult = RESULT_DTO.toBuilder().grade(50).build();

        when(resultService.save(resultDTO)).thenReturn(Mono.just(resultDTO));

        webClient
                .post().uri("/results")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidFieldResult)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void updateResult_success() {
        ResultDTO resultDTOToBeUpdated = RESULT_DTO.toBuilder().obtainedMarks(70).build();
        ResultDTO resultDTO = RESULT_DTO.toBuilder().id(ID).obtainedMarks(70).build();

        when(resultService.update(ID, resultDTOToBeUpdated)).thenReturn(Mono.just(resultDTO));

        webClient
                .put().uri("/results/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(resultDTOToBeUpdated)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ResultDTO.class)
                .isEqualTo(resultDTO);
    }

    @Test
    public void getResult_success() {
        ResultDTO resultDTO = RESULT_DTO.toBuilder().id(ID).build();

        when(resultService.getById(ID)).thenReturn(Mono.just(resultDTO));

        webClient
                .get().uri("/results/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ResultDTO.class)
                .isEqualTo(resultDTO);
    }

    @Test
    public void getResult_withNonExistingResultId_returnsNotFound() {
        String id = "test";

        when(resultService.getById(id)).thenReturn(Mono.error(new ResultRecordNotFoundException(resultRecordNotFoundErrorMessage)));

        webClient
                .get().uri("/results/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ValidationErrorResponse.class)
                .isEqualTo(new ValidationErrorResponse(List.of(new Violation(resultRecordNotFoundErrorMessage))));
    }

    @Test
    public void deletedResult_success() {

        when(resultService.delete(ID)).thenReturn(Mono.empty());

        webClient
                .delete().uri("/results/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    public void getAllResults_success() {
        ResultDTO resultDTO = RESULT_DTO.toBuilder().id(ID).build();

        when(resultService.getAll()).thenReturn(Flux.just(resultDTO));

        webClient
                .get().uri("/results")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ResultDTO.class)
                .isEqualTo(List.of(resultDTO));
    }

}
