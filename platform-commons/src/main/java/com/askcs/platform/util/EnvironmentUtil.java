package com.askcs.platform.util;

public class EnvironmentUtil{

    public static String getEnvironment() {
        return (System.getProperty( "env" ) != null ? System.getProperty( "env" ) : "Production");
    }
}
