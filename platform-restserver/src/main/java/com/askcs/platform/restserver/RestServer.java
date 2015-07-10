package com.askcs.platform.restserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.almende.eve.algorithms.clustering.GlobalAddressMapper;
import com.almende.eve.deploy.Boot;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class RestServer {
    
    private static final Logger LOG     = Logger.getLogger(RestServer.class.getName());

    public static void main(final String[] args) {
        if ( args.length == 0 ) {
            LOG.warning( "Missing argument pointing to config file:" );
            LOG.warning( "Usage: java -jar <jarfile> config" );
            return;
        }
        String configFileName = args[0];
        try {
            Config cfg = new Config();
            HazelcastInstance instance = Hazelcast.newHazelcastInstance( cfg );
            LOG.info( "Hazelcast name: " + instance.getName());
            Map<String, URI> map = instance.getMap( "agentMapper" );

            GlobalAddressMapper.set( map );

            InputStream is = new FileInputStream( new File( configFileName ) );
            Boot.boot( com.almende.eve.config.Config.getType( configFileName ), is );
        }
        catch ( FileNotFoundException e ) {
            LOG.log( Level.WARNING, "Couldn't find configfile:" + configFileName, e );
            return;
        }
        
        Server server = new Server(9000);
        server.setStopAtShutdown(true);
        WebAppContext webAppContext = new WebAppContext("src/main/webapp/WEB-INF/web.xml", "/");
        webAppContext.setResourceBase("src/main/webapp");
        server.setHandler(webAppContext);
        try {
            server.start();
            server.join();
        }
        catch (Exception ex) {
            LOG.info(String.format("Jetty is already running on port: %s. Ignoring request", 9000));
        }

    }

}
