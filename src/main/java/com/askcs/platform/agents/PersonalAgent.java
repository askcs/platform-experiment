package com.askcs.platform.agents;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.almende.eve.transform.rpc.annotation.Access;
import com.almende.eve.transform.rpc.annotation.AccessType;
import com.almende.eve.transform.rpc.annotation.Name;
import com.almende.util.TypeUtil;
import com.almende.util.callback.AsyncCallback;
import com.askcs.platform.agent.intf.PersonalAgentIntf;
import com.askcs.platform.agent.intf.TaskAgentIntf;
import com.askcs.platform.entity.Client;
import com.askcs.platform.entity.Task;
import com.askcs.platform.entity.User;

@Access(AccessType.PUBLIC)
public class PersonalAgent extends Agent implements PersonalAgentIntf {
	
	private Logger log = Logger.getLogger(PersonalAgent.class.getName());
	
	public static final String CLIENT_AGENT_TYPE = "client";
	public static final String TEAM_MEMBER_AGENT_TYPE = "teamMember";
	
	public String getDomainAgentId() {
		return getState().get("domainAgentId", String.class);
	}
	
	public void setDomainAgentId(@Name("agentId") String agentId){
		getState().put("domainAgentId", agentId);
	}
	
	public void setAgentType(@Name("agenType") String agentType) {
		setResource("agentType", agentType);
	}
	
	public String getAgentType() {
		return (String) getResource("agentType");
	}
	
	public void setUser(@Name("user") User user) {
		getState().put("user", user);
	}
	
	public User getUser() {
		return getState().get("user", User.class);
	}
	
	public void setClient(@Name("client") Client client) {
		getState().put("client", client);
	}
	
	public Client getClient() {
		return getState().get("client", Client.class);
	}
	
	// Task section
	
	public void addTask(@Name("task") Task task) {
		
		Set<String> taskIds = getTaskIds();
		if(!taskIds.contains(task.getUuid())) {
			
			TaskAgent ta = createAgent(TaskAgent.class, task.getUuid());
			ta.setTask(task);
			taskIds.add(ta.getId());
		}
		
		setTaskIds(taskIds);
	}
	
	public void removeTask(@Name("taskId") String taskId) {
		
		Set<String> taskIds = getTaskIds();
		if(taskIds.contains(taskId)) {
			
			TaskAgentIntf ta = getTaskAgent(taskId);
			ta.purge();
			
			taskIds.remove(taskId);
		}
		setTaskIds(taskIds);
	}
	
	protected void removeAllTasks() {
		Set<String> taskIds = getTaskIds();
		for(String taskId : taskIds) {
			TaskAgentIntf ta = getTaskAgent(taskId);
			ta.purge();
		}
		taskIds.clear();
		setTaskIds(taskIds);
	}
	
	public Set<String> getTaskIds() {
		Set<String> taskIds = getState().get("taskIds", new TypeUtil<Set<String>>(){});
		if(taskIds==null) {
			taskIds = new HashSet<String>();
		}
		
		return taskIds;
	}
	
	public void setTaskIds(Set<String> taskIds) {
		getState().put("taskIds", taskIds);
	}
	
	public Set<Task> getTasks(@Name("parallel") boolean parallel) {
		
		Set<String> taskIds = getTaskIds();
		if(parallel) {

			final int[] count = new int[1];
			count[0] = taskIds.size();
			final Set<Task> tasks = new HashSet<Task>();
			
			for(String id : taskIds) {
				try {
					call(getAgentUrl(id), "getTask", null, new AsyncCallback<Task>() {
						public void onSuccess(Task task) {
							synchronized (tasks) {
								tasks.add(task);
							}
							synchronized (count) {
								//log.info("Count tasks: "+count[0]);
								count[0]--;
								if(count[0]<=0) {
									synchronized (tasks) {
										tasks.notifyAll();
									}
								}
							}
						}
						
						public void onFailure(Exception exception) {
							log.warning("Failed to load task: "+exception.getMessage());
							synchronized (count) {
								count[0]--;
								if(count[0]<=0) {
									tasks.notifyAll();
								}
							}
						}
					});
				} catch (IOException e) {
					log.warning("Failed to load task e: "+e.getMessage());
				}
			}
			synchronized (tasks) {
				try {
					tasks.wait();
				} catch (InterruptedException e) {}
			}
			return tasks;
		} else {
			Set<Task> tasks = new HashSet<Task>();
			
			for(String id : taskIds) {
				TaskAgentIntf ta = getTaskAgent(id);
				tasks.add(ta.getTask());
			}
			return tasks;
		}
	}
	
	protected TaskAgentIntf getTaskAgent(String id) {
		return createAgentProxy(getAgentUrl(id), TaskAgentIntf.class);
	}
	
	public void purge() {
		removeAllTasks();
		
		destroy();
	}
}
