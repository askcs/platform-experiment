package com.askcs.platform.listener;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.almende.eve.deploy.EveListener;

public class PlatformListener extends EveListener {
	private static final Logger	LOG	= Logger.getLogger(PlatformListener.class
			.getName());
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext sc = sce.getServletContext();
		
		// Get the eve.yaml file:
		String path = sc.getInitParameter("eve_config");
		if (path != null && !path.isEmpty()) {
			final String fullname = "/WEB-INF/" + path;
			LOG.info("loading configuration file '" + sc.getRealPath(fullname)
					+ "'...");
			final InputStream is = sc.getResourceAsStream(fullname);
			if (is == null) {
				LOG.warning("Can't find the given configuration file:"
						+ sc.getRealPath(fullname));
				return;
			}
			AgentHost ah = AgentHost.getInstance();
			ah.loadAgents(is);
		}
		
	}
}
