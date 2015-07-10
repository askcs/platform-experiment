package com.askcs.platform.resources;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.askcs.platform.agent.intf.DomainAgentIntf;
import com.askcs.platform.auth.SessionHandler;
import com.askcs.platform.entity.Task;
import com.askcs.platform.restserver.agents.RestAgent;

@Path("/tasks")
public class TasksResource{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Task> getAllTasks() {
        
        String domainId = SessionHandler.getCurrentSession().getDomain();
        DomainAgentIntf da = RestAgent.getRestAgent().getDomainAgent( domainId );
        
        return da.getTasks();
    }
}
