package com.askcs.platform.agent.intf;

import com.almende.eve.protocol.jsonrpc.annotation.Name;

public interface AgentInterface extends com.almende.eve.agent.AgentInterface {

	//public void purge();
    public abstract boolean moveChildrenToHost(@Name("host") String host );
    public void deinstantiate();
}
