package com.askcs.platform.resources;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.askcs.platform.auth.LoginRequest;
import com.askcs.platform.auth.Session;
import com.askcs.platform.auth.Token;
import com.askcs.platform.restserver.agents.RestAgent;

@Path("login")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource{

    @GET
    public Token getLogin(@QueryParam("username") String username, @QueryParam("password") String password) {
        
        LoginRequest login = new LoginRequest();
        login.setUsername( username );
        login.setPassword( password );
        
        
        Session session = checkLogin( login );
        return new Token(session.getToken());
    }
    
    @POST
    public Token postLogin(LoginRequest login) {
        Session session = checkLogin( login );
        return new Token(session.getToken());
    }
    
    private Session checkLogin(LoginRequest login ) {
        
        if(!login.validUsername()) {
            throw new BadRequestException ( "Invalid username received" );
        } else if(!login.validPassword()) {
            throw new BadRequestException ( "Invalid username received" );
        }
        
        RestAgent ra = RestAgent.getRestAgent();
        String domainId = ra.login( login.getUsername(), login.getPassword());
        if(domainId!=null) {
            
            return Session.createSession( login.getUsername(), domainId );
        } else {
            throw new NotAuthorizedException( "Invalid username or password given" );
        }
    }
}
