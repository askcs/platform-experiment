package com.askcs.platform.restserver;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import com.askcs.platform.exceptionhandling.AppExceptionMapper;
import com.askcs.platform.exceptionhandling.BadRequestExceptionMapper;
import com.askcs.platform.exceptionhandling.NotAuthorizedExceptionMapper;
import com.askcs.platform.exceptionhandling.NotFoundExceptionMapper;

@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {

    public RestApplication() {
        packages( "com.askcs.platform.resources" );
        register( NotFoundExceptionMapper.class );
        register( NotAuthorizedExceptionMapper.class );
        register( AppExceptionMapper.class );
        register( BadRequestExceptionMapper.class );
    }    
}
