package com.mywhoosh.studentresultcompilation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Provides request payload details for authenticating user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthRequestDTO {

    @NotBlank(message = "Username can't be blank.")
    private String username;
    @NotBlank(message = "Password can't be blank.")
    private String password;

}