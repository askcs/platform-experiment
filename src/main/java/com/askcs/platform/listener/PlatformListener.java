package com.askcs.platform.listener;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.almende.eve.agent.AgentConfig;
import com.almende.eve.deploy.EveListener;
import com.almende.eve.instantiation.InstantiationServiceConfig;

public class PlatformListener extends EveListener {
    private static final Logger LOG = Logger.getLogger( PlatformListener.class
        .getName() );

    @Override
    public void contextInitialized( ServletContextEvent sce ) {

        final ServletContext sc = sce.getServletContext();

        // Get the eve.yaml file:
        String path = sc.getInitParameter("eve_config");
        if (path != null && !path.isEmpty()) {
                final String fullname = "/WEB-INF/" + path;
                LOG.info("loading configuration file '" + sc.getRealPath(fullname)
                                + "'...");
                final InputStream is = sc.getResourceAsStream(fullname);
                if (is == null) {
                        LOG.warning("Can't find the given configuration file:"
                                        + sc.getRealPath(fullname));
                        return;
                }
                
                AgentHost ah = AgentHost.getInstance(); 
                ah.loadConfig(is);
                
                AgentConfig defaultConfig = ah.getAgentConfig( AgentTemplate.DEFAULT );
                InstantiationServiceConfig isconfig = ah.getInstantiationServiceConfig();
                if ( isconfig != null ) {
                    PreInstantiationService pis = new PreInstantiationService( isconfig, null, defaultConfig );
                    pis.boot();
                }
        }
        
        super.contextInitialized( sce );

        LOG.info( "Initialized PlatformListener" );
    }

    @Override
    public void contextDestroyed( ServletContextEvent sce ) {

    }
}
