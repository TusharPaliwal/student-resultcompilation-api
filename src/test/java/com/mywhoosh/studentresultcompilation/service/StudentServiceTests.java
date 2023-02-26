package com.mywhoosh.studentresultcompilation.service;

import com.mywhoosh.studentresultcompilation.dto.StudentDTO;
import com.mywhoosh.studentresultcompilation.enums.StudentStatusEnum;
import com.mywhoosh.studentresultcompilation.exception.StudentNotFoundException;
import com.mywhoosh.studentresultcompilation.model.Student;
import com.mywhoosh.studentresultcompilation.repository.StudentRepository;
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
public class StudentServiceTests {

    private static final String ID_1 = "63f91c4dcf4ea943b2ab9d0d";
    private static final String ID_2 = "63f91c5acf4ea943b2ab9d0e";
    private static final String NAME_1 = "Alex";
    private static final String NAME_2 = "Larry";
    private static final String FATHER_NAME_1 = "Alex father";
    private static final String FATHER_NAME_2 = "Larry father";
    private static final int ROLL_NUMBER_1 = 4;
    private static final int ROLL_NUMBER_2 = 5;
    private static final int GRADE_1 = 2;
    private static final int GRADE_2 = 3;

    private static final Student STUDENT_1 =
            Student.builder().id(ID_1).name(NAME_1).fatherName(FATHER_NAME_1).rollNumber(ROLL_NUMBER_1).grade(GRADE_1).build();
    private static final Student STUDENT_2 =
            Student.builder().id(ID_2).name(NAME_2).fatherName(FATHER_NAME_2).rollNumber(ROLL_NUMBER_2).grade(GRADE_2).build();

    private static final StudentDTO STUDENT_DTO_1 =
            StudentDTO.builder().id(ID_1).name(NAME_1).fatherName(FATHER_NAME_1).rollNumber(ROLL_NUMBER_1).grade(GRADE_1).build();
    private static final StudentDTO STUDENT_DTO_2 =
            StudentDTO.builder().id(ID_2).name(NAME_2).fatherName(FATHER_NAME_2).rollNumber(ROLL_NUMBER_2).grade(GRADE_2).build();
    @Mock
    StudentRepository studentRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    public void findAll_success() {

        when(studentRepository.findAllByStudentStatus(StudentStatusEnum.ACTIVE)).thenReturn(Flux.just(STUDENT_1,
                STUDENT_2));
        when(modelMapper.map(STUDENT_1, StudentDTO.class)).thenReturn(STUDENT_DTO_1);
        when(modelMapper.map(STUDENT_2, StudentDTO.class)).thenReturn(STUDENT_DTO_2);


        Mono<List<StudentDTO>> studentsMono = studentService.getAll().collectList();

        StepVerifier
                .create(studentsMono)
                .assertNext(students ->
                        assertThat(List.of(STUDENT_DTO_1, STUDENT_DTO_2)).isEqualTo(students)
                )
                .verifyComplete();
    }

    @Test
    public void findAll_withNoActiveStudent_returnsEmptyList() {

        when(studentRepository.findAllByStudentStatus(StudentStatusEnum.ACTIVE)).thenReturn(Flux.empty());

        Mono<List<StudentDTO>> studentsMono = studentService.getAll().collectList();

        StepVerifier
                .create(studentsMono)
                .assertNext(students -> assertThat(List.of()).isEqualTo(students))
                .verifyComplete();
    }

    @Test
    public void getById_success() {

        when(studentRepository.findByIdAndStudentStatus(ID_1, StudentStatusEnum.ACTIVE)).thenReturn(Mono.just(STUDENT_1));
        when(modelMapper.map(STUDENT_1, StudentDTO.class)).thenReturn(STUDENT_DTO_1);

        Mono<StudentDTO> studentMono = studentService.getById(ID_1);

        StepVerifier
                .create(studentMono)
                .assertNext(student -> assertThat(STUDENT_DTO_1).isEqualTo(student))
                .verifyComplete();
    }

    @Test
    public void getById_withNoActiveStudent_returnStudentNotFoundException() {

        when(studentRepository.findByIdAndStudentStatus(ID_1, StudentStatusEnum.ACTIVE)).thenReturn(Mono.empty());

        Mono<StudentDTO> studentMono = studentService.getById(ID_1);

        StepVerifier
                .create(studentMono)
                .expectError(StudentNotFoundException.class);
    }

    @Test
    public void saveStudent_success() {
        StudentDTO studentDTOToBeSaved = STUDENT_DTO_1.toBuilder().id("").build();
        Student studentToBeSaved = STUDENT_1.toBuilder().id("").build();

        when(studentRepository.save(studentToBeSaved)).thenReturn(Mono.just(STUDENT_1));
        when(modelMapper.map(studentDTOToBeSaved, Student.class)).thenReturn(studentToBeSaved);
        when(modelMapper.map(any(), eq(StudentDTO.class))).thenReturn(STUDENT_DTO_1);

        Mono<StudentDTO> studentMono = studentService.save(studentDTOToBeSaved);

        StepVerifier
                .create(studentMono)
                .assertNext(student -> assertThat(STUDENT_DTO_1).isEqualTo(student))
                .verifyComplete();
    }

    @Test
    public void updateStudent_success() {
        String fatherNameToUpdate = "Update father name";
        StudentDTO studentDTOToBeUpdated = STUDENT_DTO_1.toBuilder().id("").fatherName(fatherNameToUpdate).build();
        Student updatedStudent = STUDENT_1.toBuilder().fatherName(fatherNameToUpdate).build();
        StudentDTO updatedStudentDto = STUDENT_DTO_1.toBuilder().fatherName(fatherNameToUpdate).build();

        when(studentRepository.findByIdAndStudentStatus(ID_1, StudentStatusEnum.ACTIVE)).thenReturn(Mono.just(STUDENT_1));
        when(studentRepository.save(STUDENT_1)).thenReturn(Mono.just(updatedStudent));
        when(modelMapper.map(updatedStudent, StudentDTO.class)).thenReturn(updatedStudentDto);

        Mono<StudentDTO> studentMono = studentService.update(ID_1, studentDTOToBeUpdated);

        StepVerifier
                .create(studentMono)
                .assertNext(student -> assertThat(updatedStudentDto).isEqualTo(student))
                .verifyComplete();
    }

    @Test
    public void updateStudent_withNoActiveStudent_returnStudentNotFoundException() {
        StudentDTO studentDTOToBeUpdated = STUDENT_DTO_1.toBuilder().id("").fatherName("Update father name").build();

        when(studentRepository.findByIdAndStudentStatus(ID_1, StudentStatusEnum.ACTIVE)).thenReturn(Mono.empty());

        Mono<StudentDTO> studentMono = studentService.update(ID_1, studentDTOToBeUpdated);

        StepVerifier
                .create(studentMono)
                .expectError(StudentNotFoundException.class);
    }

    @Test
    public void deleteStudent_success() {
        when(studentRepository.findByIdAndStudentStatus(ID_1, StudentStatusEnum.ACTIVE)).thenReturn(Mono.just(STUDENT_1));
        when(studentRepository.save(STUDENT_1)).thenReturn(Mono.just(STUDENT_1.toBuilder().studentStatus(StudentStatusEnum.DELETED).build()));

        Mono<Void> emptyMono = studentService.delete(ID_1);
        Mono<Student> studentMono = studentRepository.findByIdAndStudentStatus(ID_1, StudentStatusEnum.ACTIVE);

        StepVerifier
                .create(emptyMono)
                .expectNext()
                .verifyComplete();
        StepVerifier
                .create(studentMono)
                .assertNext(student -> assertThat(STUDENT_1.toBuilder().studentStatus(StudentStatusEnum.DELETED).build()).isEqualTo(student))
                .verifyComplete();
    }

    @Test
    public void deleteStudent_withNoActiveStudent_returnStudentNotFoundException() {
        when(studentRepository.findByIdAndStudentStatus(ID_1, StudentStatusEnum.ACTIVE)).thenReturn(Mono.empty());

        Mono<Void> studentMono = studentService.delete(ID_1);

        StepVerifier
                .create(studentMono)
                .expectError(StudentNotFoundException.class);


    }

}
