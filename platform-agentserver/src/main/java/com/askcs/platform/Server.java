package com.askcs.platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.almende.eve.algorithms.clustering.GlobalAddressMapper;
import com.askcs.platform.common.agents.HostAgent;
import com.askcs.platform.util.Boot;
import com.askcs.platform.util.EnvironmentUtil;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class Server {
    private static final Logger LOG     = Logger.getLogger(Server.class.getName());

    public static void main(final String[] args) {
        if ( args.length == 0 ) {
            LOG.warning( "Missing argument pointing to config file:" );
            LOG.warning( "Usage: java -jar <jarfile> config" );
            return;
        }
        
        String configFileName = args[0];
        try {
            Config cfg = new Config();
            NetworkConfig network = cfg.getNetworkConfig();
            JoinConfig join = network.getJoin();
            if(EnvironmentUtil.getEnvironment().equals( "Production" )) {
                join.getMulticastConfig().setEnabled( false );
                join.getTcpIpConfig().setEnabled( true )
                            .addMember( "192.168.128.4" );
            } else {
                join.getMulticastConfig().setEnabled( true );
            }
            HazelcastInstance instance = Hazelcast.newHazelcastInstance( cfg );
            LOG.info( "Hazelcast name: " + instance.getName());
            Map<String, URI> map = instance.getMap( "agentMapper" );

            GlobalAddressMapper.set( map );

            InputStream is = new FileInputStream( new File( configFileName ) );
            Boot.boot( com.almende.eve.config.Config.getType( configFileName ), is );
            
            HostAgent.getInstance();
        }
        catch ( FileNotFoundException e ) {
            LOG.log( Level.WARNING, "Couldn't find configfile:" + configFileName, e );
            return;
        }

    }
}
