package com.askcs.platform.agents;

import com.almende.eve.transform.rpc.annotation.Access;
import com.almende.eve.transform.rpc.annotation.AccessType;
import com.almende.eve.transform.rpc.annotation.Name;

@Access(AccessType.PUBLIC)
public class PersonalAgent extends Agent {
	
	public String sayName() {
		return getName();
	}
	
	protected String getName() {
		return (getState().containsKey("name") ? getState().get("name", String.class) : "No name");
	}
	
	public void setName(@Name("name") String name) {
		getState().put("name", name);
	}
}
