package com.askcs.platform.agents;

import java.util.HashSet;
import java.util.Set;

import com.almende.eve.transform.rpc.annotation.Access;
import com.almende.eve.transform.rpc.annotation.AccessType;
import com.almende.eve.transform.rpc.annotation.Name;
import com.almende.util.TypeUtil;
import com.askcs.platform.agent.intf.TeamAgentIntf;
import com.askcs.platform.entity.Team;

@Access(AccessType.PUBLIC)
public class TeamAgent extends Agent implements TeamAgentIntf {

	public String getDomainAgentId() {
		return getState().get("domainAgentId", String.class);
	}
	
	public void setDomainAgentId(@Name("agentId") String agentId){
		getState().put("domainAgentId", agentId);
	}
	
	public Team getTeam() {
		return getState().get("team", Team.class);
	}

	public void setTeam(@Name("team") Team team) {
		getState().put("team", team);
	}
	
	public void addTeamMember(@Name("teamMemberId") String teamMemberId) {
		
		Set<String> members = getTeamMemberIds();
		members.add(teamMemberId);
		setTeamMemberIds(members);
	}
	
	public void removeClient(@Name("teamMemberId") String teamMemberId) {
		
		Set<String> members = getTeamMemberIds();
		members.remove(teamMemberId);
		setTeamMemberIds(members);
	}
	
	public Set<String> getTeamMemberIds() {
		Set<String> teamMemberIds = getState().get("members", new TypeUtil<Set<String>>(){});
		if(teamMemberIds==null) {
			return new HashSet<String>();
		}
		
		return teamMemberIds;
	}
	
	public void setTeamMemberIds(@Name("teamMemberIds") Set<String> teamMemberIds){
		getState().put("members", teamMemberIds);
	}
}
