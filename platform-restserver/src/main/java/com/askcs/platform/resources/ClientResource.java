package com.askcs.platform.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.askcs.platform.agent.intf.PersonalAgentIntf;
import com.askcs.platform.entity.Client;
import com.askcs.platform.restserver.agents.RestAgent;
import com.askcs.platform.resources.accounts.TasksResource;

@Path("/client")
public class ClientResource {

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Client getClientInfo(@PathParam("id") String agentId) {
        PersonalAgentIntf pa = RestAgent.getRestAgent().getPersonalAgent( agentId );
        return pa.getClient();
    }
    
    @Path("{id}/tasks")
    public TasksResource getTaskResource(@PathParam("id") String agentId) {
        return new TasksResource(agentId);
    }
}
