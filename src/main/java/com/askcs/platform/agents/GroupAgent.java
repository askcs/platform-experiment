package com.askcs.platform.agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.almende.eve.transform.rpc.annotation.Access;
import com.almende.eve.transform.rpc.annotation.AccessType;
import com.almende.eve.transform.rpc.annotation.Name;
import com.almende.util.TypeUtil;
import com.askcs.platform.agent.intf.GroupAgentIntf;
import com.askcs.platform.entity.Group;

@Access(AccessType.PUBLIC)
public class GroupAgent extends Agent implements GroupAgentIntf {

	private Logger log = Logger.getLogger(GroupAgent.class.getSimpleName());
	
	public Group createGroup(@Name("name") String name) {

		Group group = new Group(name);
		try {
			this.addGroup(group);
		} catch (Exception e) {
			log.info("Failed to create group: "+e.getMessage());
		}
		return group;
	}

	public Group updateGroupName(@Name("id") String groupId,
			@Name("name") String name) throws Exception {
		Group group = getGroup(groupId);
		group.setName(name);
		setGroup(group);

		return group;
	}

	public void removeGroup(@Name("id") String groupId) throws Exception {
		HashMap<String, Group> groups = getGroupMap();
		if (groups == null)
			groups = new HashMap<String, Group>();

		groups.remove(groupId);
		setGroupMap(groups);
	}

	public Set<Group> getGroups() { 

		Map<String, Group> groups = this.getGroupMap();
		if (groups == null || groups.size() < 1)
			return new HashSet<Group>();

		return new HashSet<Group>(groups.values());
	}

	public Set<String> getGroupIds() {
		HashMap<String, Group> groups = getGroupMap();
		if (groups == null)
			return new HashSet<String>();

		Set<String> groupIdSet = new HashSet<String>(groups.keySet());
		return groupIdSet;
	}

	public Group getGroup(@Name("id") String id) throws Exception {
		Map<String, Group> groups = getGroupMap();

		if (!groups.containsKey(id))
			throw new Exception("Group with given id doesn't exist " + id);

		return groups.get(id);
	}

	// TODO: proper location
	public Set<Group> getGroupByName(@Name("name") String name) {
		/*Set<Group> groups = this.getGroups();
		HashSet<Group> foundGroups = new HashSet<Group>();
		for (Group group : groups) {

			if (group.getName().equals(name))
				foundGroups.add(group);
		}

		return foundGroups;*/
		return null;
	}

	public Set<String> getGroupMembers(@Name("groupId") String groupId)
			throws Exception {
		Group group = getGroup(groupId);
		return group.getMembers();
	}

	public Set<String> getGroupMembers_JOIN(
			@Name("groupIds") java.util.List<String> groupIds) throws Exception {
		java.util.HashSet<String> combined_members = new java.util.HashSet<String>();
		for (String groupId : groupIds) {
			Group group = getGroup(groupId);
			Set<String> grp_members = group.getMembers();

			for (String grp_member : grp_members)
				combined_members.add(grp_member);
		}
		return combined_members;
	}

	public Set<Group> getGroupsContainsMember(@Name("agentId") String agentId) {
		/*Set<Group> groups = this.getGroups();
		Iterator<Group> it = groups.iterator();
		while (it.hasNext()) {
			Group group = it.next();
			if (!group.hasMember(agentId))
				it.remove();
		}

		return groups;*/
		return null;
	}

	public Set<String> getAllMembers() throws Exception {
		HashSet<String> members = new HashSet<String>();
		for (Group group : this.getGroups()) {

			members.addAll(group.getMembers());
		}
		return members;
	}

	public void addMembers(@Name("groupId") String groupId,
			@Name("agentIds") Set<String> agentIds) throws Exception {
		Group group = getGroup(groupId);
		group.addMembers(agentIds);

		setGroup(group);
	}

	public void addMember(@Name("groupId") String groupId,
			@Name("agentId") String agentId) throws Exception {
		Group group = getGroup(groupId);
		group.addMember(agentId);

		setGroup(group);
	}

	public void addMemberToGroups(@Name("groupIds") Set<String> groupIds,
			@Name("agentId") String agentId) throws Exception {

		for (String groupId : groupIds) {

			Group group = getGroup(groupId);
			group.addMember(agentId);

			setGroup(group);
		}
	}

	public void removeMember(@Name("groupId") String groupId,
			@Name("agentId") String agentId) throws Exception {
		Group group = getGroup(groupId);
		group.removeMember(agentId);

		setGroup(group);
	}

	public boolean hasMember(@Name("groupId") String groupId,
			@Name("agentId") String agentId) {
		try {
			Group group = getGroup(groupId);
			return group.hasMember(agentId);
		} catch (Exception e) {
			log.warning("Failed to load group: "+e.getMessage());
		}
		
		return false;
	}

	public void removeMemberFromAllGroups(@Name("agentId") String agentId) {

		/*Set<Group> groups = this.getGroups();
		for (Group group : groups) {
			try {
				group.removeMember(agentId);
				setGroup(group);
			} catch (Exception e) {
				log.warning("Failed to remove member " + agentId
						+ " from group " + group.getId() + " e: "
						+ e.getMessage());
			}
		}*/
	}

	// //////////////

	private void addGroup(Group group) throws Exception {
		HashMap<String, Group> groups = getGroupMap();
		if (groups == null)
			groups = new HashMap<String, Group>();

		if (groups.containsKey(group.getId()))
			throw new Exception("Group with given id already exists");

		groups.put(group.getId(), group);

		setGroupMap(groups);
	}

	private void setGroup(Group group) throws Exception {
		HashMap<String, Group> groups = getGroupMap();

		if (!groups.containsKey(group.getId()))
			throw new Exception("Group with given id doesn't exist");

		groups.put(group.getId(), group);

		setGroupMap(groups);
	}

	public HashMap<String, Group> getGroupMap() {
		return getState().get("groups", new TypeUtil<HashMap<String, Group>>() {
		});
	}

	private void setGroupMap(HashMap<String, Group> groups) {
		getState().put("groups", groups);
	}

	public void purge() {
		getAgentHost().deleteAgent(getId());
	}
}
