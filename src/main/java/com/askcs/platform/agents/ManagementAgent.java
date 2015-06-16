package com.askcs.platform.agents;

import com.almende.eve.protocol.jsonrpc.annotation.Access;
import com.almende.eve.protocol.jsonrpc.annotation.AccessType;
import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.almende.eve.protocol.jsonrpc.annotation.Optional;
import com.almende.util.uuid.UUID;

public class ManagementAgent extends Agent {

	@Access(AccessType.PUBLIC)
	public String createDomainAgent(@Name("id") @Optional String id) {
		
		if(id==null) {
			id = (new UUID()).toString();
		}
		
		DomainAgent da = createAgent(DomainAgent.class, id);
		return da.getId();
	}
}
