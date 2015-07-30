package com.askcs.platform.listener;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class PreInstantiationListener implements ServletContextListener {
    private static final Logger LOG                     = Logger.getLogger(PreInstantiationListener.class
                                                                           .getName());

    @Override
    public void contextInitialized( ServletContextEvent sce ) {
    }
    
    @Override
    public void contextDestroyed( ServletContextEvent sce ) {
        LOG.info( "Context destroyed??" );
    }
}
