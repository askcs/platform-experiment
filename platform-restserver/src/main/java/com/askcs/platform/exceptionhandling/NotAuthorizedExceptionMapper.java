package com.askcs.platform.exceptionhandling;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.askcs.platform.exception.ErrorMessage;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

    public Response toResponse(NotAuthorizedException ex) {
        return Response.status(ex.getResponse().getStatus())
                        .entity(new ErrorMessage(ex))
                        .type(MediaType.APPLICATION_JSON) //this has to be set to get the generated JSON 
                        .build();
    }
}
