package com.mywhoosh.studentresultcompilation.service;

import com.mywhoosh.studentresultcompilation.model.User;
import reactor.core.publisher.Mono;

/**
 * Provides methods for managing user.
 */
public interface UserService {
    Mono<User> findByUsername(String username);
}
