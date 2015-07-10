package com.askcs.platform.auth;

public class Token {

    private String token = null;
    
    public Token() {}
    
    public Token(String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken( String token ) {
        this.token = token;
    }
}
