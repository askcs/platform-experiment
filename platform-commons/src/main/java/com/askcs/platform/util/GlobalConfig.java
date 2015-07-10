package com.askcs.platform.util;

import com.almende.eve.config.Config;

public class GlobalConfig {

    private static final GlobalConfig INSTANCE = new GlobalConfig();
    private Config config = null;
    
    private GlobalConfig() {};
    
    public static void set(Config config) {
        INSTANCE.config = config;
    }
    
    public static Config get() {
        return INSTANCE.config;
    }
}
