package com.mywhoosh.studentresultcompilation.exception;

/**
 * Custom exception for result record not found.
 */
public class ResultRecordNotFoundException extends RuntimeException{
    public ResultRecordNotFoundException(String message) {
        super(message);
    }

}
