package com.mywhoosh.studentresultcompilation.service;

import com.mywhoosh.studentresultcompilation.dto.StudentDTO;
import com.mywhoosh.studentresultcompilation.enums.StudentStatusEnum;
import com.mywhoosh.studentresultcompilation.model.Student;
import com.mywhoosh.studentresultcompilation.repository.StudentRepository;
import com.mywhoosh.studentresultcompilation.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Provides method implementation for managing student.
 */
@Service("studentService")
@Transactional
@AllArgsConstructor
@NoArgsConstructor
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${student.not-found.error-message}")
    private String studentNotFoundErrorMessage;

    @Override
    public Flux<StudentDTO> getAll() {
        return studentRepository.findAllByStudentStatus(StudentStatusEnum.ACTIVE)
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<StudentDTO> getById(final String id) {
        return studentRepository.findByIdAndStudentStatus(id, StudentStatusEnum.ACTIVE)
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .switchIfEmpty(Mono.error(new StudentNotFoundException(studentNotFoundErrorMessage)));
    }

    @Override
    public Mono<StudentDTO> update(final String id, final StudentDTO studentDTO) {
        return studentRepository.findByIdAndStudentStatus(id, StudentStatusEnum.ACTIVE)
                .switchIfEmpty(Mono.error(new StudentNotFoundException(studentNotFoundErrorMessage)))
                .map(student -> {
                    student.setName(studentDTO.getName());
                    student.setFatherName(studentDTO.getFatherName());
                    student.setGrade(studentDTO.getGrade());
                    student.setRollNumber(studentDTO.getRollNumber());
                    return student;
                })
                .flatMap(studentRepository::save)
                .map(persistedStudent -> modelMapper.map(persistedStudent,
                        StudentDTO.class));
    }

    @Override
    public Mono<StudentDTO> save(final StudentDTO studentDTO) {
        Student student = modelMapper.map(studentDTO, Student.class);
        return studentRepository.save(student)
                .map(persistedStudent -> modelMapper.map(student, StudentDTO.class));
    }

    @Override
    public Mono<Void> delete(final String id) {
        return studentRepository.findByIdAndStudentStatus(id, StudentStatusEnum.ACTIVE)
                .switchIfEmpty(Mono.error(new StudentNotFoundException(studentNotFoundErrorMessage)))
                .map(student -> {
                    student.setStudentStatus(StudentStatusEnum.DELETED);
                    return student;
                })
                .flatMap(studentRepository::save)
                .then();
    }

}
