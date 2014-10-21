package com.askcs.platform.agent.intf;

import com.askcs.platform.entity.Task;

public interface TaskAgentIntf {

	public Task getTask();
	
	public void purge();
}
