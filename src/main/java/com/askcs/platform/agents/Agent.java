package com.askcs.platform.agents;

import java.net.URI;
import java.util.HashMap;
import java.util.Set;

import com.askcs.platform.agent.intf.AgentInterface;
import com.askcs.platform.listener.AgentHost;
import com.askcs.platform.listener.AgentTemplate;

public class Agent extends com.almende.eve.agent.Agent implements AgentInterface {
	
	
	public <T extends Agent> T createAgent(Class<T> agentClass, String agentId) {
		return createAgent(agentClass, agentId, AgentTemplate.DEFAULT);
	}
	
	public <T extends Agent> T createAgent(Class<T> agentClass, String agentId, AgentTemplate template) {
		return getAgentHost().createAgent(agentClass, agentId, template);
	}
	
	public Agent getAgent(String agentId) {
		return getAgentHost().getAgent(agentId);
	}
	
	protected AgentHost getAgentHost() {
		return AgentHost.getInstance();
	}
	
	public void setResource(String key, Object value) {

        if (value == null) {
            getState().remove("resource_" + key);
        }
        else { 
            getState().put("resource_" + key, value);
        }
	}
	
	public Object getResource(String key)
	{
		return getState().get("resource_"+key, Object.class);
	}
	
	public HashMap<String, Object> getResources() {
	    
	    HashMap<String, Object> resources = new HashMap<String, Object>();
	    Set<String> keys = getState().keySet();
	    for(String key : keys) {
	        if(key.startsWith("resource_")) {
	            resources.put(key.replace("resource_", ""), getState().get(key, Object.class));
	        }
	    }
	    return resources;
	}
	
	protected URI getAgentUrl(String id) {
		return URI.create("local:"+id);
	}
	
	protected <T> T getAgent(String id, Class<T> agentInterface) {
		return createAgentProxy(getAgentUrl(id), agentInterface);
	}
	
	public void purge() {
		destroy();
	}
}
