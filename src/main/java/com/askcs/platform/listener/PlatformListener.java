package com.askcs.platform.listener;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

import com.almende.eve.deploy.EveListener;

public class PlatformListener extends EveListener {
	private static final Logger	LOG	= Logger.getLogger(PlatformListener.class
			.getName());
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		super.contextInitialized(sce);
		
		AgentHost ah = AgentHost.getInstance();
		ah.loadConfig(myConfig);
		
		LOG.info("Inialized PlatformListener");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	
	}
}
