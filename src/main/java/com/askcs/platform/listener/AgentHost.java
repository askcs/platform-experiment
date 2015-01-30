package com.askcs.platform.listener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import com.almende.eve.agent.AgentBuilder;
import com.almende.eve.agent.AgentConfig;
import com.almende.eve.capabilities.Config;
import com.almende.eve.config.YamlReader;
import com.almende.eve.instantiation.InstantiationService;
import com.almende.eve.instantiation.InstantiationServiceBuilder;
import com.almende.eve.instantiation.InstantiationServiceConfig;
import com.almende.util.jackson.JOM;
import com.askcs.platform.agents.Agent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AgentHost {
	private static final Logger	LOG	= Logger.getLogger(AgentHost.class.getName());
	
	private static AgentHost agentHost = null;
	private Map<String, ObjectNode> templates = null;
	private InstantiationServiceConfig isconfig = null;
	
	private InstantiationService is = null;
	
	public static final AgentHost getInstance() {
		if(agentHost==null) {
			agentHost = new AgentHost();
		}
		
		return agentHost;
	}
	
	private AgentHost() {
		templates = new HashMap<String, ObjectNode>();
		
	}
	
	/**
         * Load agents from input stream contain config file, agent classes should be in the classpath.
         * 
         * @param is
         *            An Inputstream to the yaml data
         */
        public void loadConfig(InputStream is) {
                loadConfig(YamlReader.load( is ));
        }
	
	/**
	 * Load agents from config file, agent classes should be in the classpath.
	 * 
	 * @param is
	 *            An Inputstream to the yaml data
	 */
	public void loadConfig(ObjectNode config) {
		loadConfig(new Config(config));
	}
	
	
	/**
	 * Load agents from config file, agent classes should be in the classpath.
	 * 
	 * @param is
	 *            An Inputstream to the yaml data
	 */
	public void loadConfig(Config config) {
		// Load the templates
	        config = config.expand();
		Iterator<String> it = config.get("templates").fieldNames();
		while(it.hasNext()) {
			String key = it.next();
			this.templates.put(key, new AgentConfig((ObjectNode) config.get("templates").get(key)));
		}
		
		// Load the instantiation service
		final ArrayNode iss = (ArrayNode) config.get("instantiationServices");
		for (final JsonNode service: iss){
			isconfig = new InstantiationServiceConfig((ObjectNode) service);
			break;
		}
	}
	
	public <T extends Agent> T createAgent(Class<T> agentClass, String agentId) {
		return createAgent(agentClass, agentId, AgentTemplate.DEFAULT);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Agent> T createAgent(Class<T> agentClass, String agentId, AgentTemplate template) {
		
		if(!agentExists(agentId)) {
			AgentConfig agentConfig = getAgentConfig(template);
			agentConfig.setClassName(agentClass.getName());
			agentConfig.setId(agentId);
			
			Agent newAgent = (Agent) new AgentBuilder().with(agentConfig).build();
			
			return (T) newAgent;
		}
		
		LOG.warning("Failed to create agent because it already exists (In initService)");
		
		return null;
	}
	
	public AgentConfig getAgentConfig(AgentTemplate template) {
	    ObjectNode config = JOM.getInstance().convertValue( this.templates.get(template.getName()), ObjectNode.class );
	    return new AgentConfig(config);
	}
	
	public InstantiationServiceConfig getInstantiationServiceConfig() {
	    return isconfig;
	}
	
	protected InstantiationService getInstantiationService() {
		if(is==null) {
			is = new InstantiationServiceBuilder().withConfig(isconfig).build();
		}
		return is;
	}
	
	public boolean agentExists(String agentId) {
		return getInstantiationService().exists(agentId);
	}
	
	public Agent getAgent(String agentId) {
		return (Agent) getInstantiationService().init(agentId);
	}
	
	public void deleteAgent(String agentId) {
            Agent agent = getAgent( agentId );
            if(agent!=null) {
                agent.purge();
            }
        }
	
	public void clear() {
		getInstantiationService().delete();
	}
}
