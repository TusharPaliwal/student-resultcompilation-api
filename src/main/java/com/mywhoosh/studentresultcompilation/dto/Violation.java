package com.mywhoosh.studentresultcompilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contains constraint violation information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Violation {
    private String message;
}
