package com.askcs.platform.agent.intf;

import java.util.Set;

import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.askcs.platform.entity.Client;
import com.askcs.platform.entity.Task;
import com.askcs.platform.entity.User;

public interface PersonalAgentIntf extends AgentInterface {
    
    public void setDomainAgentId( @Name( "agentId" ) String agentId );

    public void setUser( @Name( "user" ) User user );
    public User getUser();

    public String getAgentType();

    public Set<Task> getTasks( @Name( "parallel" ) boolean parallel );

    public Set<String> getTaskIds();

    public void addTask( @Name( "task" ) Task task );

    public void setClient( @Name( "client" ) Client client );

    public void setAgentType( @Name( "agenType" ) String agentType );

    //public void purge();
}
