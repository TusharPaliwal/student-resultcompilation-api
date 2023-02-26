package com.mywhoosh.studentresultcompilation.service;

import com.mywhoosh.studentresultcompilation.dto.ResultDTO;
import com.mywhoosh.studentresultcompilation.enums.StudentStatusEnum;
import com.mywhoosh.studentresultcompilation.exception.ResultRecordNotFoundException;
import com.mywhoosh.studentresultcompilation.exception.StudentNotFoundException;
import com.mywhoosh.studentresultcompilation.model.Result;
import com.mywhoosh.studentresultcompilation.repository.ResultRepository;
import com.mywhoosh.studentresultcompilation.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service("resultService")
@Transactional
@AllArgsConstructor
@NoArgsConstructor
public class ResultServiceImpl implements ResultService {
    private static final Comparator<Result> MARKS_COMPARATOR =
            Comparator.comparingInt(Result::getObtainedMarks).reversed();

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Value("${result.not-found.error-message}")
    private String resultRecordNotFoundErrorMessage;
    @Value("${student.not-found.error-message}")
    private String studentNotFoundErrorMessage;

    @Override
    public Flux<ResultDTO> getAll() {
        return resultRepository.findAll()
                .map(result -> modelMapper.map(result, ResultDTO.class))
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<ResultDTO> getById(final String id) {
        return resultRepository.findById(id)
                .map(result -> modelMapper.map(result, ResultDTO.class))
                .switchIfEmpty(Mono.error(new ResultRecordNotFoundException(resultRecordNotFoundErrorMessage)));
    }

    @Override
    public Mono<ResultDTO> update(final String id, final ResultDTO resultDTO) {
        return resultRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResultRecordNotFoundException(resultRecordNotFoundErrorMessage)))
                .map(result -> {
                    result.setTotalMarks(resultDTO.getTotalMarks());
                    result.setObtainedMarks(resultDTO.getObtainedMarks());
                    result.setGrade(resultDTO.getGrade());
                    result.setRollNumber(resultDTO.getRollNumber());
                    return result;
                })
                .flatMap(resultRepository::save)
                .map(persistedResult -> modelMapper.map(persistedResult,
                        ResultDTO.class));
    }

    @Override
    public Mono<ResultDTO> save(final ResultDTO resultDTO) {
        Result result = modelMapper.map(resultDTO, Result.class);

        return studentRepository.findByRollNumberAndGradeAndStudentStatus(resultDTO.getRollNumber(),
                        resultDTO.getGrade(), StudentStatusEnum.ACTIVE)
                .switchIfEmpty(Mono.error(new StudentNotFoundException(studentNotFoundErrorMessage)))
                .flatMap(persistedResult -> resultRepository.save(result))
                .map(results -> updateStudentsPosition(resultDTO.getGrade()))
                .map(persistedStudent -> modelMapper.map(result, ResultDTO.class));
    }

    @Override
    public Mono<Void> delete(final String id) {
        return resultRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResultRecordNotFoundException(resultRecordNotFoundErrorMessage)))
                .flatMap(result -> resultRepository.delete(result));

    }

    /**
     * Updates position of students based upon their marks.
     *
     * <p>If there are two records already inserted
     * in database and one new record came with more marks.
     * Then new student will get the first position and the other
     * users will get 2nd and 3rd positions
     * respectively according to their marks.</>
     *
     * @param grade for which position of students need to be updated
     * @return empty mono
     */
    private Mono<Void> updateStudentsPosition(int grade) {
        AtomicInteger counter = new AtomicInteger(1);

        // Need to make process sequential so that position changes can reflect in response.
        List<Result> results = resultRepository.findAllByGrade(grade)
                .sort(MARKS_COMPARATOR)
                .map(result -> {
                    result.setPositionInClass(counter.getAndIncrement());
                    return result;
                })
                .collectList().block();

        resultRepository.saveAll(results);

        return Mono.empty();

    }
}
