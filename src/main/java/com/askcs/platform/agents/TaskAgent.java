package com.askcs.platform.agents;

import java.util.logging.Logger;

import com.almende.eve.transform.rpc.annotation.Access;
import com.almende.eve.transform.rpc.annotation.AccessType;
import com.almende.eve.transform.rpc.annotation.Name;
import com.almende.util.TypeUtil;
import com.askcs.platform.agent.intf.TaskAgentIntf;
import com.askcs.platform.entity.Task;

@Access(AccessType.PUBLIC)
public class TaskAgent extends Agent implements TaskAgentIntf {
	
	Logger log = Logger.getLogger(TaskAgent.class.getName());

	public String getPersonalAgentId() {
		return getState().get("personalAgentId", String.class);
	}
	
	public void setPersonalAgentId(String personalAgentId) {
		getState().put("personalAgentId", personalAgentId);
	}
	
	public Task getTask() {
		Task task = getState().get("task", new TypeUtil<Task>(){});
		return task;
	}
	
	public void setTask(@Name("task") Task task) {
		getState().put("task", task);
	}
	
	public void purge() {
		deleteAgent(getId());
	}
}
