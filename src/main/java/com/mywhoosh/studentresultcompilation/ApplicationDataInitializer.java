package com.mywhoosh.studentresultcompilation;

import com.mywhoosh.studentresultcompilation.model.Student;
import com.mywhoosh.studentresultcompilation.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Loads students records at application boot-up time.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationDataInitializer {

    private final StudentRepository studentRepository;

    @EventListener(value = ApplicationReadyEvent.class)
    public void init() {
        log.info("Start data initialization...");

        Student alexStudent = Student.builder().name("Alex").fatherName("Alex father").rollNumber(10).grade(5).build();
        Student peterStudent =
                Student.builder().name("Peter").fatherName("Peter father").rollNumber(11).grade(5).build();
        Student markrStudent =
                Student.builder().name("Mark").fatherName("Mark father").rollNumber(12).grade(5).build();

        this.studentRepository.saveAll(List.of(alexStudent, peterStudent, markrStudent)).subscribe();

        log.info("End data initialization...");
    }


}
