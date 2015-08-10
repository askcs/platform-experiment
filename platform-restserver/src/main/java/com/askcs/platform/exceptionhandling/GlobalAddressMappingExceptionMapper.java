package com.askcs.platform.exceptionhandling;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.almende.eve.algorithms.clustering.GlobalAddressMappingNotFoundException;
import com.askcs.platform.exception.ErrorMessage;

public class GlobalAddressMappingExceptionMapper implements ExceptionMapper<GlobalAddressMappingNotFoundException> {

    public Response toResponse(GlobalAddressMappingNotFoundException ex) {
        return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorMessage(ex))
                        .type(MediaType.APPLICATION_JSON) //this has to be set to get the generated JSON 
                        .build();
    }

}
