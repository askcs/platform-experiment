package com.askcs.platform.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;

import com.almende.util.jackson.JOM;
import com.askcs.platform.auth.LoginRequest;
import com.askcs.platform.auth.Session;
import com.askcs.platform.auth.SessionHandler;
import com.askcs.platform.exception.ErrorMessage;
import com.askcs.platform.restserver.agents.RestAgent;
import com.fasterxml.jackson.core.JsonProcessingException;

public class AuthenticationFilter implements Filter {
    
    
    private static final Logger LOG = Logger.getLogger( AuthenticationFilter.class.getName() );

    @Override
    public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain ) throws IOException, ServletException {
        if ( authenticate( (HttpServletRequest) req, (HttpServletResponse) res ) ) {
            chain.doFilter( req, res );
            SessionHandler.clearCurrentSession();
        }
    }

    @Override
    public void init( FilterConfig filterConfig ) throws ServletException {

    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    private boolean authenticate( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        String url = req.getRequestURI();
        String[] parts = url.substring( 1 ).split( "/" );

        String path = null;
        if ( parts.length > 1 ) {
            path = parts[1];
        }

        // Only
        if ( path == null || path.isEmpty() ) {
            handleWelcome( res );
            return false;
        }
        else if ( path.toLowerCase().equals( "login" ) ) {
            return true;
        }
        else {
            
            try {
                if ( !checkSession( req ) ) {
                    throw new NotAuthorizedException( "Unauthorized: your session has expired" );
                }
            } catch (NotAuthorizedException e) {
                sendNotAuthorized( res, e );
                return false;
            }
            
            return true;
        }

    }

    private void handleWelcome( HttpServletResponse res ) {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put( "message", "Welcome to AskBackend" );
        result.put( "version", "1.0.0" );

        String resJSON;
        try {
            resJSON = JOM.getInstance().writeValueAsString( result );
            res.getWriter().append( resJSON );
        }
        catch ( JsonProcessingException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void handleLogin( HttpServletRequest req, HttpServletResponse res ) {
        
        LoginRequest login = null;
        
        if(req.getMethod().equals( "GET" )) {
            login = new LoginRequest();
            login.setUsername( req.getParameter( "username" ) );
            login.setPassword( req.getParameter( "password" ) );
            
        } else if(req.getMethod().equals( "POST" )) {
            
            StringBuffer sb = new StringBuffer();
            String line = null;
            try {
              BufferedReader reader = req.getReader();
              while ((line = reader.readLine()) != null) {
                sb.append(line);
              }
            } catch (Exception e) { 
                e.printStackTrace();
            }
            
            try {
                login = JOM.getInstance().readValue( sb.toString(), LoginRequest.class );
            } catch (IOException e ) {
                sendBadRequest( res, e.getMessage() );
                return;
            }
            
        } else {
            sendBadRequest( res, "Invalid request method" );
            return;
        }
        
        if(!login.validUsername()) {
            sendBadRequest( res, "Invalid username received" );
            return;
        } else if(!login.validPassword()) {
            sendBadRequest( res, "Invalid password received" );
            return;
        }
        
        
        
        RestAgent ra = RestAgent.getRestAgent();
        String domainId = ra.login( login.getUsername(), login.getPassword());
        if(domainId!=null) {
            
            Session session = Session.createSession( login.getUsername(), domainId );
            
            try {
                    ServletOutputStream out = res.getOutputStream();
                    out.println("{\"token\":\"" + session.getToken() + "\"}");
            } catch (Exception ex){
            }
            res.setContentType("application/json");
        } else {
            sendUnauthorized( res, "Invalid username or password given" );
        }
    }
    
    private void sendBadRequest(HttpServletResponse res, String message) {
        sendError(res, HttpServletResponse.SC_BAD_REQUEST, message);
    }

    private void sendUnauthorized(HttpServletResponse res, String message) {
        sendError(res, HttpServletResponse.SC_UNAUTHORIZED, message);
    }
    
    private void sendError(HttpServletResponse res, int statusCode ,String message) {
        
        try {
            res.sendError( statusCode, message );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sendNotAuthorized(HttpServletResponse res, NotAuthorizedException e) {
        ErrorMessage err = new ErrorMessage(e);
        
        res.setStatus( err.getStatus() );
        res.setContentType( MediaType.APPLICATION_JSON);
        try {
            ServletOutputStream out = res.getOutputStream();
            out.println(JOM.getInstance().writeValueAsString( err ));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean checkSession( HttpServletRequest req ) throws ServletException {
        String token = getToken( req );
        return SessionHandler.checkSession( token );
    }

    private String getToken( HttpServletRequest httpRequest ) throws NotAuthorizedException {
        String token = null;
        final String authorizationHeader = httpRequest.getHeader( "authorization" );
        if ( authorizationHeader == null ) {
            throw new NotAuthorizedException( "Unauthorized: No Authorization header was found" );
        }

        String[] parts = authorizationHeader.split( " " );
        if ( parts.length != 2 ) {
            throw new NotAuthorizedException( "Unauthorized: Format is Authorization: Bearer [token]" );
        }

        String scheme = parts[0];
        String credentials = parts[1];

        Pattern pattern = Pattern.compile( "^Bearer$", Pattern.CASE_INSENSITIVE );
        if ( pattern.matcher( scheme ).matches() ) {
            token = credentials;
        }
        return token;
    }
}
