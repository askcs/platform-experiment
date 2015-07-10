package com.askcs.platform.exception;

import javax.ws.rs.core.Response.Status;


public class UnAuthorizedException extends AppException{

    private static final long serialVersionUID = 8560057792304387158L;

    public UnAuthorizedException() {
        
        super(Status.UNAUTHORIZED.getStatusCode(), ErrorCode.UNAUTHORIZED.getCode(), "You do not have the right authorization for this request", "", "");
    }
}
