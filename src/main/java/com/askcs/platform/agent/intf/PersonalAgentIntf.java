package com.askcs.platform.agent.intf;

import java.util.Set;

import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.askcs.platform.entity.Task;
import com.askcs.platform.entity.User;

public interface PersonalAgentIntf {

	public void setUser(@Name("user") User user);
	public User getUser();
	
	public String getAgentType();
	
	public Set<Task> getTasks(@Name("parallel") boolean parallel);
	
	public void purge();
}
