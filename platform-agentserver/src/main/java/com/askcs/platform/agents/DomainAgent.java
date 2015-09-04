package com.askcs.platform.agents;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;

import com.almende.eve.algorithms.clustering.GlobalAddressMapper;
import com.almende.eve.protocol.jsonrpc.annotation.Access;
import com.almende.eve.protocol.jsonrpc.annotation.AccessType;
import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.almende.eve.protocol.jsonrpc.annotation.Namespace;
import com.almende.util.TypeUtil;
import com.almende.util.callback.AsyncCallback;
import com.almende.util.jackson.JOM;
import com.almende.util.uuid.UUID;
import com.askcs.platform.agent.intf.ClientGroupAgentIntf;
import com.askcs.platform.agent.intf.DomainAgentIntf;
import com.askcs.platform.agent.intf.GroupAgentIntf;
import com.askcs.platform.agent.intf.PersonalAgentIntf;
import com.askcs.platform.agent.intf.TeamAgentIntf;
import com.askcs.platform.common.agents.Agent;
import com.askcs.platform.entity.Client;
import com.askcs.platform.entity.Group;
import com.askcs.platform.entity.Task;
import com.askcs.platform.entity.Team;
import com.askcs.platform.entity.User;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Access(AccessType.PUBLIC)
public class DomainAgent extends Agent implements DomainAgentIntf {
	
	private Logger log = Logger.getLogger(DomainAgent.class.getSimpleName());  
	
	private static final String GROUP_AGENT_PREFIX = "groupAgent_";
	
	private static final String PERSONAL_AGENT_GROUP_NAME = "default";
	private static final String TEAM_MEMBER_AGENT_GROUP_NAME = "teamMembers";
	private static final String CLIENT_AGENT_GROUP_NAME = "clients";
	private static final String TEAM_AGENT_GROUP_NAME = "teams";
	private static final String CLIENT_GROUP_AGENT_GROUP_NAME = "clientGroups";
	
	private GroupAgentIntf groupAgent = null;
	
	@Override
	protected void onReady() {
	    getGroupAgent();
	}
	
	public void initTeamupEnvironment(@Name("count") int count) {
	    
            final int NR_TEAM_MEMBERS = 1;
            final int NR_CLIENTS = 1;
            final int NR_TASKS = 10;

            // Create x teams
            int index = getTeamIds().size();
            for(int i = index; i < index + count; i++) {
                addInitTeam( index, NR_CLIENTS, NR_TEAM_MEMBERS, NR_TASKS );
            }
	}
	
	protected void addInitTeam(int index, int clients, int employees, int nrOftasks) {
	    	    
	    String teamId = createTeamAgent("team_" + index);
            TeamAgentIntf ta = getLocalAgent(teamId, TeamAgentIntf.class);
            
            // Collect indexes
            int teamMemberIndex = getTeamMemberIds().size();
            int clientIndex = getClientIds().size();

            // Create team members
            for (int u = teamMemberIndex; u < teamMemberIndex + employees; u++) {

                    User user = new User("user_" + index + "_" + u,
                                    DigestUtils.md5Hex("askask"));
                    String id = createTeamMemberAgent(user);
                    ta.addTeamMember(id);
            }
            
            String clientGroupId = createClientGroupAgent("cg_" + index);

            // Link a team to a client Group
            ClientGroupAgentIntf cga = getLocalAgent(clientGroupId, ClientGroupAgentIntf.class);
            cga.addTeam(teamId);
            
            // And also the other way around
            ta.addClientGroup( clientGroupId );

            // Create clients
            for (int j = clientIndex; j < clientIndex + clients; j++) {

                    Client client = new Client("client_" + index + "_" + j, "Client", j + "");
                    String id = createClientAgent(client);
                    cga.addClient(id);

                    PersonalAgentIntf ca = getLocalAgent(id, PersonalAgentIntf.class);

                    // Add tasks
                    for (int t = 0; t < nrOftasks; t++) {

                            Task task = new Task("task_" + index + "_" + j + "_" + t, id);
                            ca.addTask(task);
                    }
            }
	}
	
	public void addTasksPerTeam(@Name("nrOfTasks") int nrOfTasks) {
	    Set<String> clientIds = getClientIds();
	    
	    for(String clientId : clientIds) {
	        PersonalAgentIntf ca = getAgent(clientId, PersonalAgentIntf.class);
	        Set<String> taskIds = ca.getTaskIds();
	        
	        int index = taskIds.size();
	        String[] split = taskIds.iterator().next().split("_");
	        int groupIndex = Integer.parseInt( split[1] );
	        int clientIndex = Integer.parseInt( split[2] );
	        
	        for (int t = index; t < index + nrOfTasks; t++) {

                    Task task = new Task("task_" + groupIndex + "_" + clientIndex + "_" + t, clientId);
                    ca.addTask(task);
                }
	    }
	}
	
	// Personal Agent section
	
	public String createClientAgent(@Name("client") Client client) {
		
		String agentId = client.getUuid();
		if(agentId == null) {
			agentId = (new UUID()).toString();
		}
		
		PersonalAgentIntf agent = createPersonalAgent(agentId);
		// Failed to create agent because agentId already exists
		if(agent!=null) {
			agent.setClient(client);
			agent.setAgentType(PersonalAgent.CLIENT_AGENT_TYPE);
			
			try {
				addMemberToGroup(CLIENT_AGENT_GROUP_NAME, agentId);
			} catch (Exception e) {
				log.warning("Failed to add client personal agent to group");
			}
		}
		
		return agent.getId();		
	}
	
	public String createTeamMemberAgent(@Name("user") User user) {
		
		String agentId = user.getUserName();
		if(agentId==null) {
			return null;
		}
		
		PersonalAgentIntf agent = createPersonalAgent(agentId);
		// Failed to create agent because agentId already exists
		if(agent!=null) {
			agent.setUser(user);
			agent.setAgentType(PersonalAgent.TEAM_MEMBER_AGENT_TYPE);
			
			try {
				addMemberToGroup(TEAM_MEMBER_AGENT_GROUP_NAME, agentId);
			} catch (Exception e) {
				log.warning("Failed to add team member personal agent to group, e: ");
				e.printStackTrace();
			}
		}
		
		return agent.getId();
	}

	protected PersonalAgentIntf createPersonalAgent(@Name("agentId") String agentId) {
		
		if(!getGroupAgent().hasMember(getGroupId(PERSONAL_AGENT_GROUP_NAME), agentId)) {
			PersonalAgentIntf agent = createAgent(PersonalAgentIntf.class, PersonalAgent.class, agentId);
			agent.setDomainAgentId(getId());
			try {
				addMemberToGroup(PERSONAL_AGENT_GROUP_NAME, agentId);
			} catch (Exception e) {
				log.warning("Failed to add personal agent to group, e: ");
				e.printStackTrace();				
			}
			
			return agent;
		}
		return null;
	}
	
	public void deletePersonalAgent(@Name("agentId") String agentId) {
		
		// TODO: Remove references
		
		/*PersonalAgentIntf pa = getPersonalAgent(agentId);
		pa.purge();*/
		
		URI url = getAgentUrl(agentId);
		try {
                    call(url, "purge", null);
                }
                catch ( IOException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
	}
	
	public Set<String> getPersonalAgentIds() {
		Set<String> personalAgentIds = new HashSet<String>();
		try {
			Group group = getGroupAgent().getGroup(getGroupId(PERSONAL_AGENT_GROUP_NAME));
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
	
	public void moveTeam(@Name("id") String id, @Name("host") String host) {
	    if(getGroupAgent().hasMember(getGroupId(TEAM_AGENT_GROUP_NAME), id)) {
	        
	    }
	}
	
	public String createTeamAgent(@Name("name") String name) {
				
		Team team = new Team(name);
		TeamAgentIntf agent = createAgent(TeamAgentIntf.class, TeamAgent.class, name);
		agent.setDomainAgentId(getId());
		agent.setTeam(team);
		String agentId = agent.getId();
		try {
			addMemberToGroup(TEAM_AGENT_GROUP_NAME, agentId);
		} catch (Exception e) {
			log.warning("Failed to add team to group, e: ");
			e.printStackTrace();			
		}
		
		return agent.getId();
	}
	
	public void deleteTeamAgent(@Name("id") String id) {
		if(getGroupAgent().hasMember(getGroupId(TEAM_AGENT_GROUP_NAME), id)) {
			
			// TODO: Remove references			
			URI url = getAgentUrl( id );
                        try {
                            call(url, "purge", null);
                        }
                        catch ( IOException e ) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
		}
	}
	
	public Set<String> getTeamIds() {
            Set<String> teamIds = new HashSet<String>();
            try {
                Group group = getGroupAgent().getGroup( getGroupId( TEAM_AGENT_GROUP_NAME ) );
                teamIds = group.getMembers();
            }
            catch ( Exception e ) {
                log.warning( "Failed to load teams, e: " );
                e.printStackTrace();
            }
    
            return teamIds;
	}
	
	public Team getTeam(@Name("teamId") String teamId) {
	    TeamAgentIntf teamAgent = getAgent( teamId, TeamAgentIntf.class );
	    return teamAgent.getTeam();
	}
	
	public Set<String> getTeamMemberIds() {
            Set<String> teamMemberIds = new HashSet<String>();
            try {
                Group group = getGroupAgent().getGroup( getGroupId( TEAM_MEMBER_AGENT_GROUP_NAME ) );
                teamMemberIds = group.getMembers();
            }
            catch ( Exception e ) {
                log.warning( "Failed to load teamMembers" );
            }
    
            return teamMemberIds;
	}
	
	protected void deleteTeamAgents() {
		Set<String> ids = getTeamIds();
		for(String agentId : ids) {
			deleteTeamAgent(agentId);
		}
	}
	
	protected TeamAgentIntf getTeamAgent(String teamId) {
	    return getAgent( teamId, TeamAgentIntf.class );
	}
	
	protected URI getTeamAgentUrl(String teamId) {
            return getAgentUrl( teamId);
        }
	
	// End Team agent section
	
	public URI getMappedAgentUrl(@Name("id")  String id) {
	    return GlobalAddressMapper.get().get( getAgentUrl(id).toASCIIString() );
	}
	
	// ClientGroup agent section
	
	public String createClientGroupAgent(@Name("name") String name) {
		
		ClientGroupAgentIntf agent = createAgent(ClientGroupAgentIntf.class, ClientGroupAgent.class, name);
		String agentId = agent.getId();
		agent.setDomainAgentId(getId());
		agent.setName(name);
		try {
			addMemberToGroup(CLIENT_GROUP_AGENT_GROUP_NAME, agentId);
		} catch (Exception e) {
			log.warning("Failed to add personal agent to group, e: ");
			e.printStackTrace();
		}
		
		return agent.getId();
	}
	
	
	public void deleteClientGroupAgent(@Name("id") String id) {
		if(getGroupAgent().hasMember(getGroupId(CLIENT_GROUP_AGENT_GROUP_NAME), id)) {
			
			// TODO: Remove references
			/*ClientGroupAgentIntf cga = getAgent(id, ClientGroupAgentIntf.class);
			cga.purge();*/
		    
		        URI url = getAgentUrl( id );
		        try {
                            call(url, "purge", null);
                        }
                        catch ( IOException e ) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
		}
	}
	
	public Set<String> getClientGroupIds() {
                Set<String> cgIds = new HashSet<String>();
                try {
                        Group group = getGroupAgent().getGroup(getGroupId(CLIENT_GROUP_AGENT_GROUP_NAME));
                        cgIds = group.getMembers();
                } catch (Exception e) {
                        log.warning("Failed to load client groups");
                }
                
                return cgIds;
        }
	
	public Set<String> getClientIds() {
                Set<String> clientIds = new HashSet<String>();
                try {
                        Group group = getGroupAgent().getGroup(getGroupId(CLIENT_AGENT_GROUP_NAME));
                        clientIds = group.getMembers();
                } catch (Exception e) {
                        log.warning("Failed to load clients");
                }
                
                return clientIds;
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
	        String groupId = getGroupId(groupName);
		getGroupAgent().addMember(groupId, agentId);
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
			Group group = getGroupAgent().createGroup(groupName);
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
			GroupAgentIntf agent = createAgent(GroupAgentIntf.class, GroupAgent.class, GROUP_AGENT_PREFIX + getId());
			groupAgentId = agent.getId();
			getState().put("groupAgentId", groupAgentId);
		}
		
		return groupAgentId;
	}
	
	@Namespace("group")
	public GroupAgentIntf getGroupAgent() {
		if(groupAgent==null) {
			groupAgent = createAgentProxy(getAgentUrl(getGroupAgentId()), GroupAgentIntf.class);
		}
		
		return groupAgent;
	}
	
	public void removeGroupAgent() {
	    try {
                call(getAgentUrl(getGroupAgentId()), "purge", null);
            }
            catch ( IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	}
	
	// End Group Agent section
	
	public Set<Task> getTasks() {
	    
	    Set<String> clientGroupIds = getClientGroupIds();
	    final int[] count = new int[1];
            count[0] = clientGroupIds.size();
            final Set<Task> allTasks = new HashSet<Task>();
            
            for(String clientGroupId : clientGroupIds) {
                    try {
                            ObjectNode params = JOM.createObjectNode();
                            params.put("parallel", true);
                            
                            call(getAgentUrl(clientGroupId), "getTasks", params, new AsyncCallback<Set<Task>>() {
                                    public void onSuccess(Set<Task> tasks) {
                                            synchronized (allTasks) {
                                                    allTasks.addAll(tasks);
                                            }
                                            synchronized (count) {
                                                    count[0]--;
                                                    if(count[0]==0) {
                                                            synchronized (allTasks) {
                                                                    allTasks.notifyAll();
                                                            }
                                                    }
                                            }
                                    }
                                    
                                    public void onFailure(Exception exception) {
                                            log.warning("Failed to load tasks e: "+exception.getMessage());
                                            synchronized (count) {
                                                    count[0]--;
                                                    if(count[0]==0) {
                                                        synchronized (allTasks) {
                                                            allTasks.notifyAll();
                                                        }
                                                    }
                                            }
                                    }
                            });
                    } catch (IOException e) {
                            log.warning("Failed to load teams e: "+e.getMessage());
                    }
            }
            synchronized (allTasks) {
                    try {
                            allTasks.wait();
                    } catch (InterruptedException e) {}
            }
            return allTasks;
	}
	
	public boolean moveTeamToHost(@Name("teamId") String teamId, @Name("host") String host) throws IOException {
	    if(getTeamIds().contains( teamId )) {
	        
	        ObjectNode params = JOM.createObjectNode();
	        params.put( "host", host);
	        
	        if(callSync(getTeamAgentUrl( teamId ), "moveChildrenToHost", params, Boolean.class)) {
	            return moveAgent( teamId, host );
	        }
	    }
	    
	    return false;
	}
	
	// FIXME: implement
	public boolean moveChildrenToHost(@Name("host") String host ) {
            return true;
        }
	
	public void purge() {
		
		// Delete personal agents
		deletePersonalAgents();
		
		// Delete client groups
		deleteClientGroupAgents();
		
		// Delete team agents
		deleteTeamAgents();
		
		// Delete group agent
		removeGroupAgent();
		
		
		destroy(false);
	}
}
