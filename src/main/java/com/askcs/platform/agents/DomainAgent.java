package com.askcs.platform.agents;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.almende.eve.transform.rpc.annotation.Access;
import com.almende.eve.transform.rpc.annotation.AccessType;
import com.almende.eve.transform.rpc.annotation.Name;
import com.almende.util.TypeUtil;
import com.almende.util.uuid.UUID;
import com.askcs.platform.agent.intf.GroupAgentIntf;
import com.askcs.platform.agent.intf.PersonalAgentIntf;
import com.askcs.platform.entity.Client;
import com.askcs.platform.entity.Group;
import com.askcs.platform.entity.Team;
import com.askcs.platform.entity.User;

@Access(AccessType.PUBLIC)
public class DomainAgent extends Agent {
	
	private Logger log = Logger.getLogger(DomainAgent.class.getSimpleName());  
	
	private static final String GROUP_AGENT_PREFIX = "groupAgent_";
	
	private static final String PERSONAL_AGENT_GROUP_NAME = "default";
	private static final String TEAM_MEMBER_AGENT_GROUP_NAME = "teamMembers";
	private static final String CLIENT_AGENT_GROUP_NAME = "clients";
	private static final String TEAM_AGENT_GROUP_NAME = "teams";
	private static final String CLIENT_GROUP_AGENT_GROUP_NAME = "clientGroups";
	
	// Personal Agent section
	
	public String createClientAgent(@Name("client") Client client) {
		
		String agentId = client.getUuid();
		if(agentId == null) {
			agentId = (new UUID()).toString();
		}
		
		String id = createPersonalAgent(agentId);
		// Failed to create agent because agentId already exists
		if(id==null) {
			PersonalAgent agent = (PersonalAgent) getAgent(id);
			agent.setClient(client);
			agent.setAgentType(PersonalAgent.CLIENT_AGENT_TYPE);
			
			try {
				addMemberToGroup(CLIENT_AGENT_GROUP_NAME, agentId);
			} catch (Exception e) {
				log.warning("Failed to add client personal agent to group");
			}
		}
		
		return id;		
	}
	
	public String createTeamMemberAgent(@Name("user") User user) {
		
		String agentId = user.getUserName();
		if(agentId==null) {
			return null;
		}
		
		String id = createPersonalAgent(agentId);
		// Failed to create agent because agentId already exists
		if(id!=null) {
			PersonalAgent agent = (PersonalAgent) getAgent(id);
			agent.setUser(user);
			agent.setAgentType(PersonalAgent.TEAM_MEMBER_AGENT_TYPE);
			
			try {
				addMemberToGroup(TEAM_MEMBER_AGENT_GROUP_NAME, agentId);
			} catch (Exception e) {
				log.warning("Failed to add team member personal agent to group");
			}
		}
		
		return id;
	}

	public String createPersonalAgent(@Name("agentId") String agentId) {
		
		if(!hasMember(getGroupId(PERSONAL_AGENT_GROUP_NAME), agentId)) {
			PersonalAgent agent = createAgent(PersonalAgent.class, agentId);
			agent.setDomainAgentId(getId());
			try {
				addMemberToGroup(PERSONAL_AGENT_GROUP_NAME, agentId);
			} catch (Exception e) {
				log.warning("Failed to add personal agent to group");
			}
			
			return agent.getId();
		}
		return null;
	}
	
	public void deletePersonalAgent(@Name("agentId") String agentId) {
		
		// TODO: Remove references
		
		PersonalAgentIntf pa = getPersonalAgent(agentId);
		pa.purge();
	}
	
	public Set<String> getPersonalAgentIds() {
		Set<String> personalAgentIds = new HashSet<String>();
		try {
			Group group = getGroup(getGroupId(PERSONAL_AGENT_GROUP_NAME));
			personalAgentIds = group.getMembers();
		} catch (Exception e) {
			log.warning("Failed to load personal agents");
		}
		
		return personalAgentIds;
	}
	
	protected void deletePersonalAgents() {
		Set<String> ids = getPersonalAgentIds();
		for(String agentId : ids) {
			deletePersonalAgent(agentId);
		}
	}
	
	protected PersonalAgentIntf getPersonalAgent(String id) {
		return createAgentProxy(getAgentUrl(id), PersonalAgentIntf.class);
	}
	
	// End PersonalAgent section
	
	// Team agent section
	
	public String createTeamAgent(@Name("name") String name) {
				
		Team team = new Team(name);
		TeamAgent agent = createAgent(TeamAgent.class, "teamAgent_" + team.getUuid());
		agent.setDomainAgentId(getId());
		agent.setTeam(team);
		String agentId = agent.getId();
		try {
			addMemberToGroup(TEAM_AGENT_GROUP_NAME, agentId);
		} catch (Exception e) {
			log.warning("Failed to add personal agent to group");
		}
		
		return agent.getId();
	}
	
	public void deleteTeamAgent(@Name("id") String id) {
		if(hasMember(getGroupId(TEAM_AGENT_GROUP_NAME), id)) {
			
			// TODO: Remove references
			
			deleteAgent(id);
		}
	}
	
	public Set<String> getTeamIds() {
		Set<String> teamIds = new HashSet<String>();
		try {
			Group group = getGroup(getGroupId(TEAM_AGENT_GROUP_NAME));
			teamIds = group.getMembers();
		} catch (Exception e) {
			log.warning("Failed to load teams");
		}
		
		return teamIds;
	}
	
	protected void deleteTeamAgents() {
		Set<String> ids = getTeamIds();
		for(String agentId : ids) {
			deleteTeamAgent(agentId);
		}
	}
	
	// End Team agent section	
	
	// ClientGroup agent section
	
	public String createClientGroupAgent(@Name("name") String name) {
		
		ClientGroupAgent agent = createAgent(ClientGroupAgent.class, "cgAgent_" + (new UUID()).toString());
		String agentId = agent.getId();
		agent.setDomainAgentId(getId());
		agent.setName(name);
		try {
			addMemberToGroup(CLIENT_GROUP_AGENT_GROUP_NAME, agentId);
		} catch (Exception e) {
			log.warning("Failed to add personal agent to group");
		}
		
		return agent.getId();
	}
	
	
	public void deleteClientGroupAgent(@Name("id") String id) {
		if(hasMember(getGroupId(CLIENT_GROUP_AGENT_GROUP_NAME), id)) {
			
			// TODO: Remove references
			
			deleteAgent(id);
		}
	}
	
	public Set<String> getClientGroupIds() {
		Set<String> cgIds = new HashSet<String>();
		try {
			Group group = getGroup(getGroupId(CLIENT_GROUP_AGENT_GROUP_NAME));
			cgIds = group.getMembers();
		} catch (Exception e) {
			log.warning("Failed to load client groups");
		}
		
		return cgIds;
	}
	
	protected void deleteClientGroupAgents() {
		Set<String> ids = getClientGroupIds();
		for(String agentId : ids) {
			deleteClientGroupAgent(agentId);
		}
	}
	
	// End ClientGroup section
	
	// Group section
	
	public void addMemberToGroup(@Name("groupName") String groupName, @Name("agentId") String agentId) throws Exception {
		addMember(getGroupId(groupName), agentId);
	}
	
	public String getGroupId(@Name("groupName") String groupName) {
		
		Map<String, String> groupMapId = getGroupIdMap();
		if(groupMapId==null) {
			groupMapId = new HashMap<String, String>();
		}
		
		// Get the id from the map
		if(groupMapId.containsKey(groupName)) {
			return groupMapId.get(groupName);
		} 
		// if the id doesn't exist the group doesn't exist so create it
		else {
			Group group = createGroup(groupName);
			groupMapId.put(groupName, group.getId());
			setGroupIdMap(groupMapId);
			
			return group.getId();
		}
	}
	
	public Map<String, String> getGroupIdMap() {
		return getState().get("groupIdMap", new TypeUtil<Map<String, String>>(){});
	}
	
	public void setGroupIdMap(@Name("groupIdMap") Map<String, String> groupIdMap) {
		getState().put("groupIdMap", groupIdMap);
	}
	
	// End Group section
	
	// Group Agent functions
	
	public String getGroupAgentId() {
		// Get the id from the state
		String groupAgentId = getState().get("groupAgentId", String.class);
		// If the id doesn't exists the agent doesn't exists so create it
		if(groupAgentId==null) {
			GroupAgent agent = createAgent(GroupAgent.class, GROUP_AGENT_PREFIX + getId());
			groupAgentId = agent.getId();
			getState().put("groupAgentId", groupAgentId);
		}
		
		return groupAgentId;
	}
	
	public GroupAgentIntf getGroupAgent() {
		return createAgentProxy(URI.create("local:"+getGroupAgentId()), GroupAgentIntf.class);
	}
	
	public Set<Group> getGroups() {
		return getGroupAgent().getGroups();
	}
	
	public Group getGroup(@Name("id") String id) throws Exception {
		return getGroupAgent().getGroup(id);
	}
	
	public Set<Group> getGroupByName(@Name("name") String name) {
		return getGroupAgent().getGroupByName(name);
	}
	
	public Group createGroup(@Name("name") String name) {
		return getGroupAgent().createGroup(name);
	}
	
	public void addMember(@Name("groupId") String groupId,
			@Name("agentId") String agentId) throws Exception {
		getGroupAgent().addMember(groupId, agentId);
	}
	
	public boolean hasMember(@Name("groupId") String groupId,
			@Name("agentId") String agentId) {
		return getGroupAgent().hasMember(groupId, agentId);
	}

	public void removeMember(@Name("groupId") String groupId,
			@Name("agentId") String agentId) throws Exception {
		getGroupAgent().removeMember(groupId, agentId);
	}
	
	// End Group Agent section
	
	public void purge() {
		
		// Delete personal agents
		deletePersonalAgents();
		
		// Delete client groups
		deleteClientGroupAgents();
		
		// Delete team agents
		deleteTeamAgents();
		
		// Delete group agent
		getGroupAgent().purge();
		
		getAgentHost().deleteAgent(getId());
	}
}
