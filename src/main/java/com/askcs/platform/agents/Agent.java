package com.askcs.platform.agents;

import java.net.URI;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import com.almende.eve.agent.AgentBuilder;
import com.almende.eve.agent.AgentConfig;
import com.almende.eve.config.Config;
import com.askcs.platform.agent.intf.AgentInterface;
import com.askcs.platform.listener.AgentTemplate;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Agent extends com.almende.eve.agent.Agent implements AgentInterface {
    private static final Logger    LOG     = Logger.getLogger(Agent.class.getName());
	
    public <T extends Agent> T createAgent( Class<T> agentClass, String agentId ) {
        return createAgent( agentClass, agentId, AgentTemplate.DEFAULT );
    }
	
    public <T extends Agent> T createAgent( Class<T> agentClass, String agentId, AgentTemplate template ) {

        //if ( !agentExists( agentId ) ) {
            
            final AgentConfig agentConfig = new AgentConfig();
            agentConfig.put("extends", "templates/"+template.getName() );
            agentConfig.setClassName( agentClass.getName() );
            agentConfig.setId( agentId );

            Agent newAgent = (Agent) new AgentBuilder()
                .withConfig( agentConfig ).build();

            return (T) newAgent;
        /*}

        LOG.warning( "Failed to create agent because it already exists (In initService)" );

        return null;*/
    }

    public void setResource( String key, Object value ) {

        if ( value == null ) {
            getState().remove( "resource_" + key );
        }
        else {
            getState().put( "resource_" + key, value );
        }
    }

    public Object getResource( String key ) {
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
    
    protected boolean agentExists(String id) {
        try {
            AgentInterface agent = getAgent( id, AgentInterface.class );
            return agent.exists();
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

    public boolean exists() {
        return true;
    }

    public void purge() {
        destroy();
    }
}
