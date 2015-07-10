package com.askcs.platform.restserver.agents;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import com.almende.eve.agent.Agent;
import com.almende.eve.protocol.jsonrpc.annotation.Access;
import com.almende.eve.protocol.jsonrpc.annotation.AccessType;
import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.almende.util.callback.AsyncCallback;
import com.almende.util.jackson.JOM;
import com.askcs.platform.agent.intf.HostAgentIntf;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;

@Access(AccessType.PUBLIC)
public class HostAgent extends Agent {

    private final Logger LOG = Logger.getLogger( HostAgent.class.getName() );
    private HazelcastInstance hazelcast = null;
    private List<String> agentHosts = null;
    
    @Override
    protected void onReady() {
        
        hazelcast = Hazelcast.getAllHazelcastInstances().iterator().next();
        agentHosts = hazelcast.getList( "agentHosts" );
        addToAgentHosts();
    }
    
    public boolean existsAnywhere( @Name( "agentId" ) String agentId ) {

        ObjectNode params = JOM.createObjectNode();
        params.put( "agentId", agentId );
        
        final AtomicBoolean result = new AtomicBoolean(false);
        final CountDownLatch count = new CountDownLatch( agentHosts.size() );
        for ( String agentHost : agentHosts) {
            try {
                URI url = URI.create(agentHost);
                call(url, "exists", params, new AsyncCallback<Boolean>() {
                    
                    public void onSuccess( Boolean exists ) {
                        if(exists) {
                            result.set(exists);
                        }
                        count.countDown();
                    }

                    public void onFailure( Exception exception ) {
                        count.countDown();
                    }
                });
            }
            catch ( IOException e ) {
                LOG.warning( "Failed to load task e: " + e.getMessage() );
            }
        }
        
        try {
            count.await();
        }
        catch ( InterruptedException e ) {
            e.printStackTrace();
        }
        
        return result.get();
    }
    
    public List<String> getAgentHosts() {
        return agentHosts;
    }
    
    protected void addToAgentHosts() {
        agentHosts.add( getHttpUrl().toASCIIString() );
    }
    
    protected URI getHttpUrl() {
        List<URI> urls = getUrls();
        for(URI url : urls) {
            if(url.getScheme().equals( "http" ) || url.getScheme().equals( "https" )) {
                return url;
            }
        }
        
        return null;
    }
    
    public String pingHost(@Name("host") String host) {
        
        URI url = URI.create(host);
        if(!agentHosts.contains( host )) {
            LOG.warning( "Host is not online" );
            return null;
        }
        HostAgentIntf ha = createAgentProxy( url, HostAgentIntf.class );
        return ha.ping();
    }
    
    public String ping() {
        return "pong";
    }
}
