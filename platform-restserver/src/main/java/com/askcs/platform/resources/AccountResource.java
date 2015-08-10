package com.askcs.platform.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.askcs.platform.agent.intf.PersonalAgentIntf;
import com.askcs.platform.entity.User;
import com.askcs.platform.restserver.agents.RestAgent;
import com.askcs.platform.resources.accounts.TasksResource;

@Path("/accounts")
public class AccountResource {

    private static final Logger LOG = Logger.getLogger( AccountResource.class.getName() );
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getAccountInfo(@PathParam("id") String agentId) {
        PersonalAgentIntf pa = RestAgent.getRestAgent().getPersonalAgent( agentId );
        return pa.getUser();
    }
    
    @Path("{id}/tasks")
    public TasksResource getTaskResource(@PathParam("id") String agentId) {
        return new TasksResource(agentId);
    }
}
