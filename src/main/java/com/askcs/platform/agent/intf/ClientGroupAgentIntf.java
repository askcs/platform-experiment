package com.askcs.platform.agent.intf;

import java.util.Set;

import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.askcs.platform.entity.Task;
import com.askcs.platform.entity.Team;

public interface ClientGroupAgentIntf extends AgentInterface {

    public void addTeam(@Name("teamId") String teamId);
    
    public Set<Team> getTeams(@Name("parallel") boolean parallel);
    
    public Set<Task> getTasks(@Name("parallel") boolean parallel);
    
    public void addClient(@Name("clientId") String clientId);
}
