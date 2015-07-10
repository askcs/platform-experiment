package com.askcs.platform.agents;


import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.almende.eve.agent.AgentConfig;
import com.almende.eve.config.Config;
import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.almende.util.callback.AsyncCallback;
import com.almende.util.jackson.JOM;
import com.askcs.platform.agent.intf.AgentInterface;
import com.askcs.platform.agent.intf.HostAgentIntf;
import com.askcs.platform.entity.AgentTemplate;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Agent extends com.almende.eve.agent.Agent implements AgentInterface {
    private static final Logger    LOG     = Logger.getLogger(Agent.class.getName());
    
    @Override
    protected void onReady() {
        LOG.info("Loaded agent: "+getId());
    }
	
    public <I extends AgentInterface> I createAgent( Class<I> agentInterface, @SuppressWarnings( "rawtypes" ) Class agentClass, String agentId ) {
        if(getHostAgent().createLocalAgent( agentClass.getName(), agentId, AgentTemplate.DEFAULT )) {
            return createAgentProxy( getLocalAgentUrl( agentId ), agentInterface );
        }
        
        return null;
    }

    public void setResource( String key, Object value ) {

        if ( value == null ) {
            getState().remove( "resource_" + key );
        }
        else {
            getState().put( "resource_" + key, value );
        }
    }

    public Object getResource(@Name("key")  String key ) {
        return getState().get( "resource_" + key, Object.class );
    }

    public HashMap<String, Object> getResources() {

        HashMap<String, Object> resources = new HashMap<String, Object>();
        Set<String> keys = getState().keySet();
        for ( String key : keys ) {
            if ( key.startsWith( "resource_" ) ) {
                resources.put( key.replace( "resource_", "" ),
                               getState().get( key, Object.class ) );
            }
        }
        return resources;
    }
    
    public abstract boolean moveChildrenToHost(@Name("host") String host );
    
    protected boolean moveAgentSet(Set<String> agentIds, final String host) {
        
        final AtomicBoolean result = new AtomicBoolean( true );
        final CountDownLatch count = new CountDownLatch( agentIds.size() );
        if(count.getCount() > 0) {
            ObjectNode params = JOM.createObjectNode();
            params.put( "host", host );
            
            // Move agents children
            for ( String agentId : agentIds ) {
                try {
                    URI url = getAgentUrl( agentId );
                    call(url, "moveChildrenToHost", params, new AsyncCallback<Boolean>() {
                        
                        private String agentId = null;
                        
                        public void onSuccess( Boolean moved ) {
                            if(agentId!=null) {
                                try {
                                    if(!moveAgent( agentId, host )) {
                                        result.set( false );
                                    }
                                }
                                catch ( IOException e ) {
                                    e.printStackTrace();
                                }
                            } else {
                                LOG.warning( "AgentId is still empty!!!" );
                            }
                            count.countDown();
                        }
    
                        public void onFailure( Exception exception ) {
                            count.countDown();
                            result.set( false );
                        }
                        
                        public AsyncCallback<Boolean> setAgentId( String agentId ) {
                            this.agentId = agentId;
                            return this;
                        }
                    }.setAgentId( agentId ) );
                }
                catch ( IOException e ) {
                    LOG.warning( "Failed to load task e: " + e.getMessage() );
                }
            }
            
            try {
                LOG.info( "Waiting for moving to finish" );
                count.await();
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        
        return result.get();
    }
    
    protected boolean moveAgent(final String id, final String host) throws IOException {
        /*ObjectNode params = JOM.createObjectNode();
        params.put( "id", id );
        params.put( "host", host );
        
        return callSync(getHostAgentUrl(), "moveAgentToHost", params, Boolean.class);*/
        
        return getHostAgent().moveAgentToHost( id, host );
    }
    
    protected AgentConfig getAgentConfig(AgentTemplate template) {
        ObjectNode agentConfig = null;
        try {
            ObjectNode config = Config.getGlobal();
            if(config.get( "templates" ).has( template.getName() )) {
                //agentConfig = JOM.getInstance().convertValue( config.get( "templates" ).get(template.getName()).deepCopy(), ObjectNode.class );
                agentConfig = config.get( "templates" ).get(template.getName()).deepCopy();
            }
        } catch (Exception e) {
            LOG.info( "Failed to load template" );
        }
        return AgentConfig.decorate( agentConfig );
    }
    
    protected HostAgentIntf getHostAgent() {
        return getLocalAgent( "host_agent", HostAgentIntf.class );
    }
    
    protected URI getHostAgentUrl(){
        return getLocalAgentUrl( "host_agent" );
    }
    
    protected boolean agentExists(String id) {
        try {
            HostAgentIntf hostAgent = getHostAgent();
            return hostAgent.exists( id );
        } catch (Exception e) {
            LOG.warning( "Failed to call agent: " + id + " e: " + e.getMessage() );
        }
        return false;
    }

    protected URI getAgentUrl( String id ) {
        String url = "eve:" + id;
        return URI.create( url );
    }
    
    protected URI getLocalAgentUrl( String id ) {
        String url = "local:" + id;
        return URI.create( url );
    }
    
    protected <T extends AgentInterface> T getAgent( String id, Class<T> agentInterface ) {
        return createAgentProxy( getAgentUrl( id ), agentInterface );
    }

    protected <T extends AgentInterface> T getLocalAgent( String id, Class<T> agentInterface ) {
        return createAgentProxy( getLocalAgentUrl( id ), agentInterface );
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
    
    public void deinstantiate() {
        destroy(true);
    }

    public void purge() {
        destroy(false);
    }
}
