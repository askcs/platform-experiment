package com.askcs.platform.agents;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.almende.eve.protocol.jsonrpc.annotation.Access;
import com.almende.eve.protocol.jsonrpc.annotation.AccessType;
import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.almende.util.TypeUtil;
import com.almende.util.callback.AsyncCallback;
import com.almende.util.jackson.JOM;
import com.askcs.platform.agent.intf.AgentInterface;
import com.askcs.platform.agent.intf.ClientGroupAgentIntf;
import com.askcs.platform.agent.intf.PersonalAgentIntf;
import com.askcs.platform.agent.intf.TeamAgentIntf;
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

    public Set<Team> getTeams( @Name( "parallel" ) boolean parallel ) {

        final Set<Team> teams = new HashSet<Team>();
        Set<String> teamIds = getTeamIds();

        final int[] count = new int[1];
        count[0] = teamIds.size();

        if ( parallel ) {
            for ( String teamId : teamIds ) {
                try {
                    call( getAgentUrl( teamId ), "getTeam", null, new AsyncCallback<Team>() {
                        public void onSuccess( Team team ) {
                            log.info( "add team" );
                            count[0]--;
                            teams.add( team );
                            if ( count[0] == 0 ) {
                                teams.notifyAll();
                            }
                        }

                        public void onFailure( Exception exception ) {
                            log.warning( "Failed to load teams" );
                        }
                    } );
                }
                catch ( IOException e ) {
                    log.warning( "Failed to load teams e: " + e.getMessage() );
                }
            }
            synchronized ( teams ) {
                try {
                    teams.wait();
                }
                catch ( InterruptedException e ) {
                }
            }
            return teams;
        }
        else {
            for ( String teamId : teamIds ) {
                TeamAgentIntf ta = getTeamAgent( teamId );
                teams.add( ta.getTeam() );
            }
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

    public Set<Task> getTasks( @Name( "parallel" ) boolean parallel ) {

        Set<String> clientIds = getClientIds();

        if ( parallel ) {
            final int[] count = new int[1];
            count[0] = clientIds.size();
            final Set<Task> allTasks = new HashSet<Task>();

            for ( String clientId : clientIds ) {
                try {
                    ObjectNode params = JOM.createObjectNode();
                    params.put( "parallel", parallel );

                    call( getAgentUrl( clientId ), "getTasks", params, new AsyncCallback<Set<Task>>() {
                        public void onSuccess( Set<Task> tasks ) {
                            synchronized ( allTasks ) {
                                allTasks.addAll( tasks );
                            }
                            synchronized ( count ) {
                                //log.info("Count: "+count[0]);
                                count[0]--;
                                if ( count[0] == 0 ) {
                                    synchronized ( allTasks ) {
                                        allTasks.notifyAll();
                                    }
                                }
                            }
                        }

                        public void onFailure( Exception exception ) {
                            log.warning( "Failed to load tasks e: " + exception.getMessage() );
                            synchronized ( count ) {
                                count[0]--;
                                if ( count[0] == 0 ) {
                                    allTasks.notifyAll();
                                }
                            }
                        }
                    } );
                }
                catch ( IOException e ) {
                    log.warning( "Failed to load teams e: " + e.getMessage() );
                }
            }
            synchronized ( allTasks ) {
                try {
                    allTasks.wait();
                }
                catch ( InterruptedException e ) {
                }
            }
            return allTasks;
        }
        else {
            Set<Task> allTasks = new HashSet<Task>();
            for ( String clientId : clientIds ) {
                PersonalAgentIntf ca = getClientAgent( clientId );
                Set<Task> tasks = ca.getTasks( parallel );
                allTasks.addAll( tasks );

                //log.info("Added tasks from: "+clientId);
            }
            return allTasks;
        }
    }
    
    public boolean moveChildrenToHost(@Name("host") String host ) {
        
        // Move clients
        Set<String> clientIds = getClientIds();
        return moveAgentSet( clientIds, host );
    }
    
    

    // Client Agent section
}
