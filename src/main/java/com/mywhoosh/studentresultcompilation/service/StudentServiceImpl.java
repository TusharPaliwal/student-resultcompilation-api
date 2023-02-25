package com.mywhoosh.studentresultcompilation.service;

import com.mywhoosh.studentresultcompilation.dto.StudentDTO;
import com.mywhoosh.studentresultcompilation.enums.StudentStatusEnum;
import com.mywhoosh.studentresultcompilation.exception.StudentNotFoundException;
import com.mywhoosh.studentresultcompilation.model.Student;
import com.mywhoosh.studentresultcompilation.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

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
                .switchIfEmpty(Mono.error(new StudentNotFoundException("Student not found!")));
    }

    @Override
    public Mono<StudentDTO> update(final String id, final StudentDTO studentDTO) {
        return studentRepository.findByIdAndStudentStatus(id, StudentStatusEnum.ACTIVE)
                .switchIfEmpty(Mono.error(new StudentNotFoundException("Student not found!")))
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
                .switchIfEmpty(Mono.error(new StudentNotFoundException("Student not found!")))
                .map(student -> {
                    student.setStudentStatus(StudentStatusEnum.DELETED);
                    return student;
                })
                .flatMap(studentRepository::save)
                .then();
    }

}
