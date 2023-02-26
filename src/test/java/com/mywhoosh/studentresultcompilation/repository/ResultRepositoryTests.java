package com.mywhoosh.studentresultcompilation.repository;

import com.mywhoosh.studentresultcompilation.enums.RemarkEnum;
import com.mywhoosh.studentresultcompilation.model.Result;
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
public class ResultRepositoryTests {

    private static final int OBTAINED_MARKS = 60;
    private static final int TOTAL_MARKS = 100;
    private static final int ROLL_NUMBER = 3;
    private static final int GRADE = 1;
    private static final Result RESULT_TO_BE_PERSISTED =
            Result.builder().obtainedMarks(OBTAINED_MARKS).totalMarks(TOTAL_MARKS).rollNumber(ROLL_NUMBER).grade(GRADE).build();
    @Autowired
    ResultRepository resultRepository;

    @Test
    public void findAllByGrade_success() {
        Result result = RESULT_TO_BE_PERSISTED.toBuilder().rollNumber(4).build();
        Result differentGradeStudentResult = RESULT_TO_BE_PERSISTED.toBuilder().grade(2).build();

        resultRepository.deleteAll().block();
        resultRepository.save(result).block();
        resultRepository.save(differentGradeStudentResult).block();
        resultRepository.save(RESULT_TO_BE_PERSISTED).block();

        Mono<List<Result>> resultsMono =
                resultRepository.findAllByGrade(GRADE).collectList();

        StepVerifier
                .create(resultsMono)
                .assertNext(results ->
                        assertThat(List.of(result, RESULT_TO_BE_PERSISTED)).usingRecursiveComparison().ignoringFields("id").isEqualTo(results)
                )
                .verifyComplete();
    }


    @Test
    public void saveStudent_withPassMarks_returnsResultWithPassRemarks() {
        Mono<Result> resultMono = resultRepository.save(RESULT_TO_BE_PERSISTED);

        StepVerifier
                .create(resultMono)
                .assertNext(result ->
                        assertThat(RESULT_TO_BE_PERSISTED.toBuilder().remark(RemarkEnum.PASSED).positionInClass(1).build()).usingRecursiveComparison().ignoringFields("id").isEqualTo(result)
                )
                .verifyComplete();
    }

    @Test
    public void saveStudent_withFailMarks_returnsResultWithFailRemarks() {
        Mono<Result> resultMono = resultRepository.save(RESULT_TO_BE_PERSISTED.toBuilder().obtainedMarks(40).build());

        StepVerifier
                .create(resultMono)
                .assertNext(result ->
                        assertThat(RESULT_TO_BE_PERSISTED.toBuilder().remark(RemarkEnum.FAILED).obtainedMarks(40).positionInClass(1).build()).usingRecursiveComparison().ignoringFields("id").isEqualTo(result)
                )
                .verifyComplete();
    }

    @Test
    public void saveStudent_withInvalidFields_throwsValidationError() {
        Mono<Result> resultMono =
                resultRepository.save(RESULT_TO_BE_PERSISTED.toBuilder().grade(50).build());

        StepVerifier
                .create(resultMono)
                .expectError(ConstraintViolationException.class);
    }

}
