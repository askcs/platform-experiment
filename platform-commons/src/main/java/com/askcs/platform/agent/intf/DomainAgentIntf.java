package com.askcs.platform.agent.intf;

import java.util.Set;

import com.askcs.platform.entity.Task;

public interface DomainAgentIntf extends AgentInterface {

    public Set<Task> getTasks();
}
