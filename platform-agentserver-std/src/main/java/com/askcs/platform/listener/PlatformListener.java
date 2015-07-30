package com.askcs.platform.listener;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.almende.eve.algorithms.clustering.GlobalAddressMapper;
import com.almende.eve.deploy.Boot;
import com.almende.eve.deploy.EveListener;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class PlatformListener extends EveListener {
    private static final Logger LOG = Logger.getLogger( PlatformListener.class
        .getName() );

    /*@Override
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
                //ah.loadConfig(is);
                
                AgentConfig defaultConfig = ah.getAgentConfig( AgentTemplate.DEFAULT );
                InstantiationServiceConfig isconfig = ah.getInstantiationServiceConfig();
                if ( isconfig != null ) {
                    PreInstantiationService pis = new PreInstantiationService( isconfig, null, defaultConfig );
                    pis.boot();
                }
        }
        
        super.contextInitialized( sce );

        LOG.info( "Initialized PlatformListener" );
    }*/
    
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
    
                /*ClientConfig clientConfig = new ClientConfig();
                //clientConfig.addAddress("127.0.0.1:5701");
                HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
                IMap<String, URI> map = client.getMap("customers");*/
                
                Config cfg = new Config();
                HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
                Map<String, URI> map = instance.getMap("agentMapper");
                
                GlobalAddressMapper.set( map );
                
                Boot.boot("yaml", is);
                
        }
    }

    @Override
    public void contextDestroyed( ServletContextEvent sce ) {

    }
}
