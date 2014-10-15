package com.askcs.platform.agents;

import com.askcs.platform.listener.AgentHost;

public class Agent extends com.almende.eve.agent.Agent {

	public void storeConfig() {
		getState().put("config", getConfig());
	}
	
	public <T extends Agent> T createAgent(Class<T> agentClass, String agentId) {
		return getAgentHost().createAgent(agentClass, agentId);
	}
	
	public void deleteAgent(String agentId) {
		getAgentHost().deleteAgent(agentId);
	}
	
	protected AgentHost getAgentHost() {
		return AgentHost.getInstance();
	}
}
