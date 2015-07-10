package com.askcs.platform.resources.accounts;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.askcs.platform.agent.intf.PersonalAgentIntf;
import com.askcs.platform.entity.Task;
import com.askcs.platform.restserver.agents.RestAgent;

public class TasksResource {
    
    private final String accountId;
    
    public TasksResource(final String accountId) {
        this.accountId = accountId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Task> getAllTasks() {
        
        PersonalAgentIntf pa = RestAgent.getRestAgent().getPersonalAgent( accountId );  
        return pa.getTasks();
    }
}
