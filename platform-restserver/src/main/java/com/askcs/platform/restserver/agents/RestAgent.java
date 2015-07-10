package com.askcs.platform.restserver.agents;

import java.util.Set;

import com.almende.eve.agent.AgentBuilder;
import com.almende.eve.agent.AgentConfig;
import com.almende.util.jackson.JOM;
import com.askcs.platform.agent.intf.DomainAgentIntf;
import com.askcs.platform.agent.intf.HostAgentIntf;
import com.askcs.platform.agent.intf.PersonalAgentIntf;
import com.askcs.platform.agents.Agent;
import com.askcs.platform.entity.AgentTemplate;
import com.askcs.platform.entity.Task;
import com.askcs.platform.entity.User;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RestAgent extends Agent {

    private static RestAgent INSTANCE = null;
    
    public static RestAgent getRestAgent() {
        
        if(INSTANCE==null) {
            
            ObjectNode config = JOM.createObjectNode();
            config.put("extends", "templates/"+AgentTemplate.DEFAULT.getName() );
            
            final AgentConfig agentConfig = AgentConfig.decorate(config);
            agentConfig.setClassName( RestAgent.class.getName() );
            agentConfig.setId( "restagent" );
            
            INSTANCE = (RestAgent) new AgentBuilder().withConfig(agentConfig).build();
        }
        
        return INSTANCE;
    }
    
    public String login(String username, String password) {
        
        HostAgentIntf ha = getHostAgent();
        if(ha.existsAnywhere( username )) {
            PersonalAgentIntf pa = getPersonalAgent( username );
            if(pa.getType().contains("PersonalAgent") && pa.checkPassword( password )) {
                return pa.getDomainAgentId();
            }
        }
        
        return null;
    }
    
    public User getUser(String agentId) {
        PersonalAgentIntf pa = getPersonalAgent( agentId );
        return pa.getUser();
    }
    
    /*public Set<Task> getTasks(String domainId) {
        DomainAgentIntf da = getDomainAgent( domainId );
        return da.getTasks();
    }*/
    
    public PersonalAgentIntf getPersonalAgent(String agentId) {
        return getAgent( agentId, PersonalAgentIntf.class );
    }
    
    public DomainAgentIntf getDomainAgent(String agentId) {
        return getAgent( agentId, DomainAgentIntf.class );
    }
    
    @Override
    public boolean moveChildrenToHost( String host ) {
        return false;
    }
}
