package com.askcs.platform.agent.intf;

import java.util.Set;

import com.almende.eve.protocol.jsonrpc.annotation.Access;
import com.almende.eve.protocol.jsonrpc.annotation.AccessType;
import com.almende.eve.protocol.jsonrpc.annotation.Name;
import com.askcs.platform.entity.Group;

@Access(AccessType.PUBLIC)
public interface GroupAgentIntf extends AgentInterface {

	/**/
    // location/domain specific calls
    public Group createGroup (@Name("name") String name );
    public Set<Group> getGroupByName (@Name("name") String name);
    public Set<Group> getGroups ();
    public Set<String> getGroupIds ( );
    public Set<String> getAllMembers () throws Exception;
    public Set<Group> getGroupsContainsMember (@Name("agentId") String agentId);
/**/
	
    
    //group management
    public void removeGroup (@Name("id") String groupId) throws Exception;
    public Group getGroup (@Name("id") String id) throws Exception;
    public Group updateGroupName (@Name("id") String groupId, @Name("name") String name) throws Exception;

    //member management
    public void addMember (@Name("groupId") String groupId, @Name("agentId") String agentId) throws Exception;
    public void removeMember (@Name("groupId") String groupId, @Name("agentId") String agentId) throws Exception;
    public boolean hasMember (@Name("groupId") String groupId, @Name("agentId") String agentId);

    // single member | multiple groups
    public void addMemberToGroups (@Name("groupIds") Set<String> groupIds, @Name("agentId") String agentId) throws Exception;

    // single group | multiple members
    public void addMembers (@Name("groupId") String groupId, @Name("agentIds") Set<String> agentIds) throws Exception;
    public Set<String> getGroupMembers (@Name("groupId") String groupId) throws Exception;

    // single member | all groups
    public void removeMemberFromAllGroups (@Name("agentId") String agentId);


    // multiple members | multiple groups
    public Set<String> getGroupMembers_JOIN (@Name("groupIds") java.util.List<String> groupIds) throws Exception;

    // agent methods
    //public void purge();
}
