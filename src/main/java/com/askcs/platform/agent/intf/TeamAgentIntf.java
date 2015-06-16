package com.askcs.platform.agent.intf;

import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.askcs.platform.entity.Team;

public interface TeamAgentIntf extends AgentInterface {

	public Team getTeam();
	
	public void addTeamMember(@Name("teamMemberId") String teamMemberId);
}
