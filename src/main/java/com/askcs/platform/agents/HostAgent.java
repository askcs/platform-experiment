package com.askcs.platform.agents;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import com.almende.eve.agent.AgentBuilder;
import com.almende.eve.agent.AgentConfig;
import com.almende.eve.instantiation.InstantiationService;
import com.almende.eve.instantiation.InstantiationServiceBuilder;
import com.almende.eve.instantiation.InstantiationServiceConfig;
import com.almende.eve.protocol.jsonrpc.annotation.Access;
import com.almende.eve.protocol.jsonrpc.annotation.AccessType;
import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.almende.eve.protocol.jsonrpc.annotation.Optional;
import com.almende.util.jackson.JOM;
import com.almende.util.uuid.UUID;
import com.askcs.platform.GlobalConfig;
import com.askcs.platform.agent.intf.AgentInterface;
import com.askcs.platform.agent.intf.DomainAgentIntf;
import com.askcs.platform.agent.intf.HostAgentIntf;
import com.askcs.platform.listener.AgentTemplate;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Access(AccessType.PUBLIC)
public class HostAgent extends Agent implements HostAgentIntf {

    private final Logger LOG = Logger.getLogger( HostAgent.class.getName() );
    private InstantiationService is = null;
    private HazelcastInstance hazelcast = null;
    private List<String> agentHosts = null;
    
    @Override
    protected void onReady() {
        final ObjectNode service = (ObjectNode) GlobalConfig.get().get("instantiationService");

        if(service!=null) {
            final InstantiationServiceConfig isconfig = InstantiationServiceConfig
                            .decorate((ObjectNode) service);
            is = new InstantiationServiceBuilder().withConfig(isconfig).build();
            is.boot();
        } else {
            LOG.warning( "No instantiation service loaded!" );
        }
        
        hazelcast = Hazelcast.getAllHazelcastInstances().iterator().next();
        agentHosts = hazelcast.getList( "agentHosts" );
        addToAgentHosts();
    }
    
    public String createDomainAgent( @Name( "id" ) @Optional String id ) {

        if ( id == null ) {
            id = ( new UUID() ).toString();
        }

        DomainAgentIntf da = createAgent(DomainAgentIntf.class, DomainAgent.class, id );
        return da.getId();
    }

    public boolean createLocalAgent( @Name("agentClassName") String agentClassName, @Name("agentId") String agentId, 
                                @Name("template") AgentTemplate template ) {

        if ( !exists( agentId ) ) {
            
            ObjectNode config = JOM.createObjectNode();
            config.put("extends", "templates/"+template.getName() );
            
            final AgentConfig agentConfig = AgentConfig.decorate(config);
            agentConfig.setClassName( agentClassName );
            agentConfig.setId( agentId );
            
            is.register( agentConfig.getId(), agentConfig, agentConfig.getClassName() );

            Agent agent = (Agent) is.init( agentConfig.getId() );
            if(agent.getId()!=null) {
                return true;
            }
        }

        LOG.warning( "Failed to create agent because it already exists (In initService)" );

        return false;
    }
    
    public boolean exists(@Name("agentId") String agentId) {
        return is.exists( agentId );
    }
    
    public List<String> getAgentHosts() {
        return agentHosts;
    }
    
    public boolean moveAgentToHost(@Name("id") String agentId, @Name("host") String host) {
        
        if(agentHosts.contains( host ) && exists( agentId )) {
            URI url = URI.create(host);

            AgentInterface agent = getLocalAgent( agentId, AgentInterface.class );
            String className = agent.getType();
            
            Boolean removed = removeAgentInstance( agentId );
            if(removed) {                
                /*HostAgentIntf ha = createAgentProxy( url, HostAgentIntf.class );
                ha.addAgentInstance( agentId, agent.getType() );*/
                
                ObjectNode params = JOM.createObjectNode();
                params.put("id", agentId);
                params.put("className", className);
                try {
                    return callSync(url, "addAgentInstance", params, Boolean.class);
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } else {
            LOG.warning("Agent: " + agentId + " does not exists in this host: " + getHttpUrl().toASCIIString());
        }
        
        return false;
    }
    
    protected boolean removeAgentInstance(@Name("id") String agentId) {
        URI url = getLocalAgentUrl( agentId );
        if(url!=null) {
            try {
                call(url, "deinstantiate", null);
                return true;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    public Boolean addAgentInstance(@Name("id") String agentId, @Name("className") String className) {
        ObjectNode config = JOM.createObjectNode();
        config.put("extends", "templates/defaultAgent" );
        
        final AgentConfig params = AgentConfig.decorate(config);
        params.setClassName( className );
        params.setId( agentId );
        
        is.register( agentId, params, className );
        is.init(agentId);
        
        return true;
    }
    
    protected void addToAgentHosts() {
        agentHosts.add( getHttpUrl().toASCIIString() );
    }
    
    public boolean moveChildrenToHost(@Name("host") String host ) {
        return true;
    }
}
