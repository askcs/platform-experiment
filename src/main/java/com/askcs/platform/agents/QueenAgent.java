package com.askcs.platform.agents;

import java.util.ArrayList;
import java.util.List;

import com.almende.eve.transform.rpc.annotation.Access;
import com.almende.eve.transform.rpc.annotation.AccessType;
import com.almende.eve.transform.rpc.annotation.Name;
import com.almende.util.TypeUtil;

@Access(AccessType.PUBLIC)
public class QueenAgent extends Agent {

	public String createPersonalAgent(@Name("agentId") String agentId, @Name("name") String name) {
		
		PersonalAgent agent = createAgent(PersonalAgent.class, agentId);
		agent.setName(name);
		
		addAgent(agent.getId());
		
		return agent.getId();
	}
	
	public void deletePersonalAgent(@Name("agentId") String agentId) {
		deleteAgent(agentId);
	}
	
	public List<String> getAgents() {
		List<String> agents = getState().get("agents", new TypeUtil<List<String>>(){});
		if(agents==null) {
			agents = new ArrayList<String>();
		}
		return agents;
	}
	
	public void addAgent(String id) {
		List<String> agents = getAgents();
		agents.add(id);
		setAgents(agents);
	}
	
	public void setAgents(List<String> agents) {
		getState().put("agents", agents);
	}
}
