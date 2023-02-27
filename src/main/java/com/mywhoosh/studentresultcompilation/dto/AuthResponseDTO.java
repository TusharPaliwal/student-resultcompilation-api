package com.mywhoosh.studentresultcompilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Provides response details for authenticated user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthResponseDTO {
    private String token;

}