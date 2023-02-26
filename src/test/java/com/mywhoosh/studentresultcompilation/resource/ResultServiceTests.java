package com.mywhoosh.studentresultcompilation.resource;

import com.mywhoosh.studentresultcompilation.dto.ResultDTO;
import com.mywhoosh.studentresultcompilation.enums.RemarkEnum;
import com.mywhoosh.studentresultcompilation.enums.StudentStatusEnum;
import com.mywhoosh.studentresultcompilation.exception.ResultRecordNotFoundException;
import com.mywhoosh.studentresultcompilation.exception.StudentNotFoundException;
import com.mywhoosh.studentresultcompilation.model.Result;
import com.mywhoosh.studentresultcompilation.model.Student;
import com.mywhoosh.studentresultcompilation.repository.ResultRepository;
import com.mywhoosh.studentresultcompilation.repository.StudentRepository;
import com.mywhoosh.studentresultcompilation.service.ResultServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ResultServiceTests {

    private static final String ID_1 = "63f91c4dcf4ea943b2ab9d0d";
    private static final String ID_2 = "63f91c5acf4ea943b2ab9d0e";
    private static final String NAME = "Larry";
    private static final String FATHER_NAME = "Larry father";
    private static final int OBTAINED_MARKS_1 = 60;
    private static final int OBTAINED_MARKS_2 = 70;
    private static final int TOTAL_MARKS = 100;
    private static final int ROLL_NUMBER_1 = 4;
    private static final int ROLL_NUMBER_2 = 5;
    private static final int GRADE_1 = 2;
    private static final int GRADE_2 = 3;

    private static final Result RESULT_1 =
            Result.builder().id(ID_1).obtainedMarks(OBTAINED_MARKS_1).totalMarks(TOTAL_MARKS).rollNumber(ROLL_NUMBER_1).grade(GRADE_1).positionInClass(1).remark(RemarkEnum.PASSED).build();
    private static final Result RESULT_2 =
            Result.builder().id(ID_2).obtainedMarks(OBTAINED_MARKS_2).totalMarks(TOTAL_MARKS).rollNumber(ROLL_NUMBER_2).grade(GRADE_2).positionInClass(1).remark(RemarkEnum.PASSED).build();

    private static final ResultDTO RESULT_DTO_1 =
            ResultDTO.builder().id(ID_1).obtainedMarks(OBTAINED_MARKS_1).totalMarks(TOTAL_MARKS).rollNumber(ROLL_NUMBER_1).grade(GRADE_1).build();
    private static final ResultDTO RESULT_DTO_2 =
            ResultDTO.builder().id(ID_2).obtainedMarks(OBTAINED_MARKS_2).totalMarks(TOTAL_MARKS).rollNumber(ROLL_NUMBER_2).grade(GRADE_2).build();

    private static final Student STUDENT =
            Student.builder().id(ID_1).name(NAME).fatherName(FATHER_NAME).rollNumber(ROLL_NUMBER_1).grade(GRADE_1).build();

    @Mock
    ResultRepository resultRepository;
    @Mock
    StudentRepository studentRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    private ResultServiceImpl resultService;

    @Test
    public void findAll_success() {

        when(resultRepository.findAll()).thenReturn(Flux.just(RESULT_1,
                RESULT_2));
        when(modelMapper.map(RESULT_1, ResultDTO.class)).thenReturn(RESULT_DTO_1);
        when(modelMapper.map(RESULT_2, ResultDTO.class)).thenReturn(RESULT_DTO_2);


        Mono<List<ResultDTO>> resultsMono = resultService.getAll().collectList();

        StepVerifier
                .create(resultsMono)
                .assertNext(results ->
                        assertThat(List.of(RESULT_DTO_1, RESULT_DTO_2)).isEqualTo(results)
                )
                .verifyComplete();
    }

    @Test
    public void findAll_withNoResult_returnsEmptyList() {

        when(resultRepository.findAll()).thenReturn(Flux.empty());

        Mono<List<ResultDTO>> resultsMono = resultService.getAll().collectList();

        StepVerifier
                .create(resultsMono)
                .assertNext(results -> assertThat(List.of()).isEqualTo(results))
                .verifyComplete();
    }

    @Test
    public void getById_success() {

        when(resultRepository.findById(ID_1)).thenReturn(Mono.just(RESULT_1));
        when(modelMapper.map(RESULT_1, ResultDTO.class)).thenReturn(RESULT_DTO_1);

        Mono<ResultDTO> resultMono = resultService.getById(ID_1);

        StepVerifier
                .create(resultMono)
                .assertNext(result -> assertThat(RESULT_DTO_1).isEqualTo(result))
                .verifyComplete();
    }

    @Test
    public void getById_withNoResult_returnResultRecordNotFoundException() {

        when(resultRepository.findById(ID_1)).thenReturn(Mono.empty());

        Mono<ResultDTO> resultMono = resultService.getById(ID_1);

        StepVerifier
                .create(resultMono)
                .expectError(ResultRecordNotFoundException.class);
    }

    @Test
    public void saveResult_success() {
        ResultDTO resultDTOToBeSaved = RESULT_DTO_1.toBuilder().id("").build();
        Result resultToBeSaved = RESULT_1.toBuilder().id("").build();

        when(resultRepository.save(resultToBeSaved)).thenReturn(Mono.just(RESULT_1));
        when(resultRepository.findAllByGrade(GRADE_1)).thenReturn(Flux.just(RESULT_1));
        when(studentRepository.findByRollNumberAndGradeAndStudentStatus(ROLL_NUMBER_1, GRADE_1, StudentStatusEnum.ACTIVE)).thenReturn(Mono.just(STUDENT));
        when(modelMapper.map(resultDTOToBeSaved, Result.class)).thenReturn(resultToBeSaved);
        when(modelMapper.map(any(), eq(ResultDTO.class))).thenReturn(RESULT_DTO_1);

        Mono<ResultDTO> resultMono = resultService.save(resultDTOToBeSaved);

        StepVerifier
                .create(resultMono)
                .assertNext(result -> assertThat(RESULT_DTO_1).isEqualTo(result))
                .verifyComplete();
    }

    @Test
    public void saveResult_withHigherMarksStudent_studentPositionChanges() {
        ResultDTO resultDTOToBeSaved = RESULT_DTO_1.toBuilder().id("").build();
        Result resultToBeSaved = RESULT_1.toBuilder().id("").build();

        Result secondPositionResult = RESULT_1.toBuilder().obtainedMarks(55).rollNumber(7).build();
        Result thirdPositionResult = RESULT_1.toBuilder().obtainedMarks(53).rollNumber(9).build();

        when(resultRepository.save(resultToBeSaved)).thenReturn(Mono.just(RESULT_1));
        when(resultRepository.findAllByGrade(GRADE_1)).thenReturn(Flux.just(RESULT_1, secondPositionResult,
                thirdPositionResult));
        when(studentRepository.findByRollNumberAndGradeAndStudentStatus(ROLL_NUMBER_1, GRADE_1, StudentStatusEnum.ACTIVE)).thenReturn(Mono.just(STUDENT));
        when(modelMapper.map(resultDTOToBeSaved, Result.class)).thenReturn(resultToBeSaved);
        when(modelMapper.map(any(), eq(ResultDTO.class))).thenReturn(RESULT_DTO_1);

        Mono<ResultDTO> resultMono = resultService.save(resultDTOToBeSaved);

        StepVerifier
                .create(resultMono)
                .assertNext(result -> assertThat(RESULT_DTO_1).isEqualTo(result))
                .verifyComplete();

        Assertions.assertEquals(2, secondPositionResult.getPositionInClass());
        Assertions.assertEquals(3, thirdPositionResult.getPositionInClass());
    }

    @Test
    public void saveResult_withNonExistingRollNumberAndGrade_returnStudentNotFoundException() {
        int rollNumber = 18;
        int grade = 8;
        ResultDTO resultDTOToBeSaved = RESULT_DTO_1.toBuilder().id("").rollNumber(rollNumber).grade(grade).build();

        when(studentRepository.findByRollNumberAndGradeAndStudentStatus(rollNumber, grade, StudentStatusEnum.ACTIVE)).thenReturn(Mono.empty());

        Mono<ResultDTO> resultMono = resultService.save(resultDTOToBeSaved);

        StepVerifier
                .create(resultMono)
                .expectError(StudentNotFoundException.class);
    }

    @Test
    public void updateResult_success() {
        int obtainedMarks = 75;
        ResultDTO resultDTOToBeUpdated = RESULT_DTO_1.toBuilder().id("").obtainedMarks(obtainedMarks).build();
        Result updatedResult = RESULT_1.toBuilder().obtainedMarks(obtainedMarks).build();
        ResultDTO updatedResultDto = RESULT_DTO_1.toBuilder().obtainedMarks(obtainedMarks).build();

        when(resultRepository.findById(ID_1)).thenReturn(Mono.just(RESULT_1));
        when(resultRepository.save(RESULT_1)).thenReturn(Mono.just(updatedResult));
        when(modelMapper.map(updatedResult, ResultDTO.class)).thenReturn(updatedResultDto);

        Mono<ResultDTO> resultMono = resultService.update(ID_1, resultDTOToBeUpdated);

        StepVerifier
                .create(resultMono)
                .assertNext(result -> assertThat(updatedResultDto).isEqualTo(result))
                .verifyComplete();
    }

    @Test
    public void updateResult_withNonExistingResult_returnResultRecordNotFoundException() {
        ResultDTO resultDTOToBeUpdated = RESULT_DTO_1.toBuilder().id("").obtainedMarks(65).build();

        when(resultRepository.findById(ID_1)).thenReturn(Mono.empty());

        Mono<ResultDTO> resultMono = resultService.update(ID_1, resultDTOToBeUpdated);

        StepVerifier
                .create(resultMono)
                .expectError(ResultRecordNotFoundException.class);
    }

    @Test
    public void deleteResult_success() {
        when(resultRepository.findById(ID_1)).thenReturn(Mono.just(RESULT_1));
        when(resultRepository.delete(RESULT_1)).thenReturn(Mono.empty());

        Mono<Void> emptyMono = resultService.delete(ID_1);

        StepVerifier
                .create(emptyMono)
                .expectNext()
                .verifyComplete();
    }

    @Test
    public void deleteStudent_withNonExistingResult_returnResultRecordNotFoundException() {
        when(resultRepository.findById(ID_1)).thenReturn(Mono.empty());

        Mono<Void> resultMono = resultService.delete(ID_1);

        StepVerifier
                .create(resultMono)
                .expectError(ResultRecordNotFoundException.class);


    }

}
