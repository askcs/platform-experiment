package com.askcs.platform.auth;

public class LoginRequest{

    private String username = null;
    private String password = null;
    
    public LoginRequest() {}
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername( String username ) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword( String password ) {
        this.password = password;
    }
    
    public boolean validUsername() {
        if(this.username!=null && !this.username.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    public boolean validPassword() {
        if(this.password!=null && !this.password.isEmpty()) {
            return isPasswordMD5();
        }
        
        return false;
    }
    
    private boolean isPasswordMD5() {
        return this.password.matches("[a-fA-F0-9]{32}");
    }
    
}


