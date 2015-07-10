package com.askcs.platform.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.almende.util.jackson.JOM;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Session {
    
    private Logger LOG = Logger.getLogger(Session.class.getName());

    private static final String KEY = "3ktb7%Z~jIc~RoS^W672PQ472.v[0";
    private static final int SESSION_DURATION = 8 * 60 * 60 * 1000; // 8 hours 
    private String agentId = null;
    private String domain = null;
    private long createdAt = -1;
    
    public Session() {}
    
    private Session(String agentId, String domain) {
        this.agentId = agentId;
        this.domain = domain;
        this.createdAt = DateTime.now().getMillis();
    }
    
    public static Session createSession(String agentId, String domain) {
        return new Session(agentId, domain);
    }
    
    public static Session fromToken(String token) {
        
        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey( KEY ).parseClaimsJws(token);
            Session session = JOM.getInstance().readValue( jws.getBody().getSubject(), Session.class );
            return session;
        
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId( String agentId ) {
        this.agentId = agentId;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain( String domain ) {
        this.domain = domain;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt( long createdAt ) {
        this.createdAt = createdAt;
    }
    
    @JsonIgnore
    public String getToken() {
        return Jwts.builder().setSubject( toJSON() ).signWith(SignatureAlgorithm.HS512, KEY).compact();
    }
    
    @JsonIgnore
    public boolean isExpired() {
        if(DateTime.now().getMillis() <= this.createdAt + SESSION_DURATION) {
            LOG.info( "Session expires at: " + new DateTime(this.createdAt + SESSION_DURATION).toString() );
            return false;
        }
        
        LOG.info( "Session is expired at: " + new DateTime(this.createdAt + SESSION_DURATION).toString() );
        
        return true;
    }
    
    @JsonIgnore
    public String toJSON() {
        try {
            return JOM.getInstance().writeValueAsString( this );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
