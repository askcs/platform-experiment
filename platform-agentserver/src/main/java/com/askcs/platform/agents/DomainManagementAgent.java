package com.askcs.platform.agents;

import com.almende.eve.protocol.jsonrpc.annotation.Access;
import com.almende.eve.protocol.jsonrpc.annotation.AccessType;
import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.almende.eve.protocol.jsonrpc.annotation.Optional;
import com.almende.util.uuid.UUID;
import com.askcs.platform.agent.intf.DomainAgentIntf;
import com.askcs.platform.common.agents.Agent;

public class DomainManagementAgent extends Agent {

    @Access(AccessType.PUBLIC)
    public String createDomainAgent( @Name( "id" ) @Optional String id ) {

        if ( id == null ) {
            id = ( new UUID() ).toString();
        }

        DomainAgentIntf da = createAgent(DomainAgentIntf.class, DomainAgent.class, id );
        return da.getId();
    }

    @Override
    public boolean moveChildrenToHost( String host ) {
        // TODO Auto-generated method stub
        return false;
    }
}
