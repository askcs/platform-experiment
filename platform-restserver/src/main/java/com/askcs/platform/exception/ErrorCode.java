package com.askcs.platform.exception;

public enum ErrorCode {

    UNAUTHORIZED(1);
    
    private int code = -1;
    
    private ErrorCode( int code ) {
        this.code = code;
    }
    
    public int getCode() {
        return this.code;
    }
}
