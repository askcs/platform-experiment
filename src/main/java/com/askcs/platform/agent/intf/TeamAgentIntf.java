package com.askcs.platform.agent.intf;

import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.askcs.platform.entity.Team;

public interface TeamAgentIntf extends AgentInterface {

	public Team getTeam();
	
	public void setTeam(@Name("team") Team team);
	
	public void addTeamMember(@Name("teamMemberId") String teamMemberId);
	
	public void addClientGroup( @Name( "clientGroupId" ) String clientGroupId );
	
	public void setDomainAgentId(@Name("agentId") String agentId);
}
