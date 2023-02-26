package com.mywhoosh.studentresultcompilation.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Provides configuration for application.
 */
@Configuration
public class StudentResultCompilationConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
