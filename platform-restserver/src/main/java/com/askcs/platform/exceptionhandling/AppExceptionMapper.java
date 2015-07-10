package com.askcs.platform.exceptionhandling;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.askcs.platform.exception.AppException;
import com.askcs.platform.exception.ErrorMessage;

@Provider
public class AppExceptionMapper implements ExceptionMapper<AppException> {

    public Response toResponse(AppException ex) {
        return Response.status(ex.getStatus())
                        .entity(new ErrorMessage(ex))
                        .type(MediaType.APPLICATION_JSON).
                        build();
    }
}
