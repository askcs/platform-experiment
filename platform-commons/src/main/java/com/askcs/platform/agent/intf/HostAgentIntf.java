package com.askcs.platform.agent.intf;

import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.askcs.platform.entity.AgentTemplate;

public interface HostAgentIntf extends AgentInterface {
    
    public boolean exists(@Name("agentId") String agentId);
    
    public boolean existsAnywhere( @Name( "agentId" ) String agentId );
    
    public boolean createLocalAgent( @Name("agentClassName") String agentClassName, @Name("agentId") String agentId, 
                                @Name("template") AgentTemplate template );
    
    public Boolean addAgentInstance(@Name("id") String agentId, @Name("className") String className);
    
    public boolean moveAgentToHost(@Name("id") String agentId, @Name("host") String host);
    
    public String ping();
}
