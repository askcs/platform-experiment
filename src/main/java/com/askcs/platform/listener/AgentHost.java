package com.askcs.platform.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.almende.eve.agent.AgentBuilder;
import com.almende.eve.agent.AgentConfig;
import com.almende.eve.capabilities.Config;
import com.almende.eve.config.YamlReader;
import com.almende.eve.state.State;
import com.almende.eve.state.StateBuilder;
import com.almende.eve.state.file.FileStateConfig;
import com.askcs.platform.agents.Agent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AgentHost {
	private static final Logger	LOG	= Logger.getLogger(AgentHost.class.getName());
	
	private static AgentHost agentHost = null;
	private Map<String, Agent> agents = null;
	private Config config = null;
	private Map<String, AgentConfig> configs = null;
		
	public static final AgentHost getInstance() {
		if(agentHost==null) {
			agentHost = new AgentHost();
		}
		
		return agentHost;
	}
	
	private AgentHost() {
		agents = new HashMap<String, Agent>();
		configs = new HashMap<String, AgentConfig>();
	}
	
	/**
	 * Load agents from config file, agent classes should be in the classpath.
	 * 
	 * @param configFileName
	 *            the config file name
	 */
	protected Config loadConfig(String configFileName) {
		try {
			InputStream is = new FileInputStream(new File(configFileName)); 
			return loadConfig(is);
		} catch (FileNotFoundException e) {
			LOG.log(Level.WARNING,
					"Couldn't find configfile:" + configFileName, e);
			return null;
		}
	}
	
	/**
	 * Load agents from config file, agent classes should be in the classpath.
	 * 
	 * @param is
	 *            An Inputstream to the yaml data
	 */
	protected Config loadConfig(final InputStream is) {
		config = YamlReader.load(is).expand();
		Iterator<String> it = config.get("templates").fieldNames();
		while(it.hasNext()) {
			String key = it.next();
			this.configs.put(key, new AgentConfig((ObjectNode) config.get("templates").get(key)));
		}
		return config;
	}
	
	/**
	 * Load agents from config file, agent classes should be in the classpath.
	 * 
	 * @param configFileName
	 *            the config file name
	 */
	public void loadAgents(String configFileName) {
		loadConfig(configFileName);
		loadAgents();
	}
	
	/**
	 * Load agents from config file, agent classes should be in the classpath.
	 * This variant can load WakeableAgents.
	 * 
	 * @param configFileName
	 *            the config file name
	 */
	public void loadAgents(final InputStream is) {
		loadConfig(is);
		loadAgents();
	}
	
	/**
	 * Load agents.
	 * 
	 * @param is
	 *            An Inputstream to the yaml data
	 * @param ws
	 *            The WakeService
	 * @param cl
	 *            the custom classloader
	 */
	public void loadAgents() {
		
		AgentConfig dac = this.configs.get("defaultAgent");
		// Assumption??
		FileStateConfig stateConfig = new FileStateConfig(dac.getState());
		File folder = new File(stateConfig.getPath());
		if(folder.listFiles()!=null) {
			for (final File fileEntry : folder.listFiles()) {
		        if (!fileEntry.isDirectory()) {
		        	stateConfig.setId(fileEntry.getName());
		        	State state = new StateBuilder().withConfig(stateConfig).build();
		        	
		        	ObjectNode ac = state.get("config", ObjectNode.class);
		        	if(ac!=null) {
			        	AgentConfig agentConfig = new AgentConfig(ac);
						Agent newAgent = (Agent) new AgentBuilder().with(agentConfig).build();
						newAgent.storeConfig();
						LOG.info("Created agent:" + newAgent.getId());
						this.agents.put(newAgent.getId(), newAgent);
		        	}
		        }
		    }
		}
		
		
		final ArrayNode agents = (ArrayNode) config.get("agents");
		if(agents!=null) {
			for (final JsonNode agent : agents) {
				if(!this.agents.containsKey(agent.get("id").asText())) {
					AgentConfig agentConfig = new AgentConfig((ObjectNode) agent);
					Agent newAgent = (Agent) new AgentBuilder().with(agentConfig).build();
					newAgent.storeConfig();
					LOG.info("Created agent:" + newAgent.getId());
					this.agents.put(newAgent.getId(), newAgent);
				}
			}
		}
	}
	
	public void unloadAgents() {
		this.agents.clear();
	}
	
	public <T extends Agent> T createAgent(Class<T> agentClass, String agentId) {
		return createAgent(agentClass, agentId, AgentTemplate.DEFAULT);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Agent> T createAgent(Class<T> agentClass, String agentId, AgentTemplate template) {
		if(!this.agents.containsKey(agentId)) {
			
			AgentConfig agentConfig = this.configs.get(template.getName());
			agentConfig.setClassName(agentClass.getName());
			agentConfig.setId(agentId);
			
			Agent newAgent = (Agent) new AgentBuilder().with(agentConfig).build();
			newAgent.storeConfig();
			
			this.agents.put(newAgent.getId(), newAgent);
			
			return (T) newAgent;
		}
		
		LOG.severe("Failed to create agent because id already exists");
		return null;
	}
	
	public void deleteAgent(String id) {
		Agent agent = getAgent(id);
		if(agent!=null) {
			
			agent.getScheduler().clear();
			agent.getState().delete();
			this.agents.remove(id);
		}
	}
	
	public Agent getAgent(String id) {
		if(agents.containsKey(id)) {
			return agents.get(id);
		}
		return null;
	}
}
