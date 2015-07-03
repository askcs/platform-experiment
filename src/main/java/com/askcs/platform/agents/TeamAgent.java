package com.askcs.platform.agents;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.almende.eve.protocol.jsonrpc.annotation.Access;
import com.almende.eve.protocol.jsonrpc.annotation.AccessType;
import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.almende.util.TypeUtil;
import com.almende.util.callback.AsyncCallback;
import com.almende.util.jackson.JOM;
import com.askcs.platform.agent.intf.PersonalAgentIntf;
import com.askcs.platform.agent.intf.TeamAgentIntf;
import com.askcs.platform.entity.Team;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Access(AccessType.PUBLIC)
public class TeamAgent extends Agent implements TeamAgentIntf {
    
        private static final Logger LOG = Logger.getLogger( TeamAgent.class.getName() );

        //Domain
    
	public String getDomainAgentId() {
		return getState().get("domainAgentId", String.class);
	}
	
	public void setDomainAgentId(@Name("agentId") String agentId) {
		getState().put("domainAgentId", agentId);
	}
	
	// Team
	
	public Team getTeam() {
		return getState().get("team", Team.class);
	}

	public void setTeam(@Name("team") Team team) {
		getState().put("team", team);
	}
	
	// ClientGroup
	
        public Set<String> getClientGroupIds() {
            Set<String> clientGroupIds = getState().get( "clientGroupIds",new TypeUtil<Set<String>>() {} );
            if ( clientGroupIds == null ) {
                return new HashSet<String>();
            }
    
            return clientGroupIds;
        }
    
        public void setClientGroupIds( @Name( "clientGroupIds" ) Set<String> clientGroupIds ) {
            getState().put( "clientGroupIds", clientGroupIds );
        }
        
        public void addClientGroup( @Name( "clientGroupId" ) String clientGroupId ) {
            Set<String> clientGroupIds = getClientGroupIds();
            clientGroupIds.add( clientGroupId );
            setClientGroupIds( clientGroupIds );
        }
	
	// Team member
	
	public void addTeamMember(@Name("teamMemberId") String teamMemberId) {
		
		Set<String> members = getTeamMemberIds();
		members.add(teamMemberId);
		setTeamMemberIds(members);
	}
	
	public void removeTeamMember(@Name("teamMemberId") String teamMemberId) {
		
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
	
	protected PersonalAgentIntf getTeamMemberAgent(String id) {
            return createAgentProxy(getAgentUrl(id), PersonalAgentIntf.class);
        }
	
	
	@Override
	public boolean moveChildrenToHost(@Name("host") String host ) {
	    
	    boolean result = true;
	    result = moveTeamMembers(host);
	    result = moveClientGroups( host );
	    return result;
	}
	
	private boolean moveTeamMembers(final String host) {
	    Set<String> teamMemberIds = getTeamMemberIds();
	    return moveAgentSet( teamMemberIds, host );
	}

	private boolean moveClientGroups(final String host) {
	    Set<String> clientGroupIds = getClientGroupIds();
	    return moveAgentSet( clientGroupIds, host );
	}
}
