package com.askcs.platform.agents;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import com.almende.eve.protocol.jsonrpc.annotation.Access;
import com.almende.eve.protocol.jsonrpc.annotation.AccessType;
import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.almende.util.TypeUtil;
import com.almende.util.callback.AsyncCallback;
import com.almende.util.jackson.JOM;
import com.askcs.platform.agent.intf.ClientGroupAgentIntf;
import com.askcs.platform.agent.intf.PersonalAgentIntf;
import com.askcs.platform.agent.intf.TeamAgentIntf;
import com.askcs.platform.agents.Agent;
import com.askcs.platform.entity.Task;
import com.askcs.platform.entity.Team;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Access(AccessType.PUBLIC)
public class ClientGroupAgent extends Agent implements ClientGroupAgentIntf {
	
    private static Logger log = Logger.getLogger( ClientGroupAgent.class.getName() );

    public String getName() {
        return (String) getResource( "name" );
    }

    public void setName( @Name( "name" ) String name ) {
        setResource( "name", name );
    }

    public String getDomainAgentId() {
        return getState().get( "domainAgentId", String.class );
    }

    public void setDomainAgentId( @Name( "agentId" ) String agentId ) {
        getState().put( "domainAgentId", agentId );
    }

    // Team Agent section

    public void addTeam( @Name( "teamId" ) String teamId ) {
        Set<String> teamIds = getTeamIds();
        teamIds.add( teamId );
        setTeamIds( teamIds );
    }

    public void removeTeam( @Name( "teamId" ) String teamId ) {
        Set<String> teamIds = getTeamIds();
        teamIds.remove( teamId );
        setTeamIds( teamIds );
    }

    public Set<String> getTeamIds() {
        Set<String> teamIds = getState().get( "teamIds", new TypeUtil<Set<String>>() {
        } );
        if ( teamIds == null ) {
            return new HashSet<String>();
        }

        return teamIds;
    }
	
    public void setTeamIds( @Name( "teamIds" ) Set<String> teamIds ) {
        getState().put( "teamIds", teamIds );
    }

    public Set<Team> getTeams() {

        final Set<Team> teams = new HashSet<Team>();
        final Set<String> teamIds = getTeamIds();

        final CountDownLatch count = new CountDownLatch( teamIds.size() );

        for ( String teamId : teamIds ) {
            try {
                call( getAgentUrl( teamId ), "getTeam", null, new AsyncCallback<Team>() {
                    public void onSuccess( Team team ) {
                        synchronized ( teams ) {
                            log.info( "add team" );
                            teams.add( team );
                        }
                        count.countDown();
                    }

                    public void onFailure( Exception exception ) {
                        log.warning( "Failed to load teams" );
                        count.countDown();
                    }
                } );
            }
            catch ( IOException e ) {
                log.warning( "Failed to load teams e: " + e.getMessage() );
            }
        }
        try {
            count.await();
        }
        catch ( InterruptedException e ) {
        }
        return teams;
    }

    protected TeamAgentIntf getTeamAgent( String id ) {
        return createAgentProxy( getAgentUrl( id ), TeamAgentIntf.class );
    }

    // End Team Agent section

    // Client Agent section

    public void addClient( @Name( "clientId" ) String clientId ) {
        Set<String> clientIds = getClientIds();
        clientIds.add( clientId );
        setClientIds( clientIds );
    }

    public void removeClient( @Name( "clientId" ) String clientId ) {
        Set<String> clientIds = getClientIds();
        clientIds.remove( clientId );
        setClientIds( clientIds );
    }

    public Set<String> getClientIds() {
        Set<String> clientIds = getState().get( "clientIds", new TypeUtil<Set<String>>() {
        } );
        if ( clientIds == null ) {
            return new HashSet<String>();
        }

        return clientIds;
    }

    public void setClientIds( @Name( "clientIds" ) Set<String> clientIds ) {
        getState().put( "clientIds", clientIds );
    }

    protected PersonalAgentIntf getClientAgent( String id ) {
        return createAgentProxy( getAgentUrl( id ), PersonalAgentIntf.class );
    }

    public Set<Task> getTasks() {

        final Set<String> clientIds = getClientIds();

        final CountDownLatch count = new CountDownLatch(clientIds.size());
        final Set<Task> allTasks = new HashSet<Task>();

        for ( String clientId : clientIds ) {
            try {

                call( getAgentUrl( clientId ), "getTasks", null, new AsyncCallback<Set<Task>>() {
                    public void onSuccess( Set<Task> tasks ) {
                        synchronized ( allTasks ) {
                            allTasks.addAll( tasks );
                        }
                        count.countDown();
                    }

                    public void onFailure( Exception exception ) {
                        log.warning( "Failed to load tasks e: " + exception.getMessage() );
                        count.countDown();
                    }
                } );
            }
            catch ( IOException e ) {
                log.warning( "Failed to load teams e: " + e.getMessage() );
            }
        }
        try {
            count.await();
        }
        catch ( InterruptedException e ) {
        }
        return allTasks;
    }
    
    public boolean moveChildrenToHost(@Name("host") String host ) {
        
        // Move clients
        Set<String> clientIds = getClientIds();
        return moveAgentSet( clientIds, host );
    }
    
    

    // Client Agent section
}
