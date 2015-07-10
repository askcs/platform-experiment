package com.askcs.platform.agent.intf;

import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.askcs.platform.entity.Task;

public interface TaskAgentIntf extends AgentInterface {
    
        public void setPersonalAgentId(String personalAgentId);
        
        public void setTask(@Name("task") Task task);

	public Task getTask();
	
	public void purge();
}
