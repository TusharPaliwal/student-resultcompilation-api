package com.mywhoosh.studentresultcompilation.service;

import com.mywhoosh.studentresultcompilation.enums.RoleEnum;
import com.mywhoosh.studentresultcompilation.model.User;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides methods implementation for managing user.
 */
@Service("userService")
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private Map<String, User> data;

    @PostConstruct
    public void init() {
        data = new HashMap<>();

        //username:passwowrd -> user:user
        data.put("user", new User("user", "cBrlgyL2GI2GINuLUUwgojITuIufFycpLG4490dhGtY=", true,
                List.of(RoleEnum.ROLE_USER)));

        //username:passwowrd -> admin:admin
        data.put("admin", new User("admin", "dQNjUIMorJb8Ubj2+wVGYp6eAeYkdekqAcnYp+aRq5w=", true,
                List.of(RoleEnum.ROLE_ADMIN)));
    }

    @Override
    // TODO : Implement service to find user from database.
    public Mono<User> findByUsername(String username) {
        return Mono.justOrEmpty(data.get(username));
    }

}
