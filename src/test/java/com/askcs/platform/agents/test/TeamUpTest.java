package com.askcs.platform.agents.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.almende.eve.config.YamlReader;
import com.almende.util.uuid.UUID;
import com.askcs.platform.agents.ClientGroupAgent;
import com.askcs.platform.agents.DomainAgent;
import com.askcs.platform.agents.PersonalAgent;
import com.askcs.platform.agents.TeamAgent;
import com.askcs.platform.entity.Client;
import com.askcs.platform.entity.Task;
import com.askcs.platform.entity.User;
import com.askcs.platform.listener.AgentHost;

public class TeamUpTest {

	private AgentHost			ah				= null;
	private static final String	DOMAIN_AGENT_ID	= "domain";

	private DomainAgent			da				= null;

	@Before
	public void setUp() throws Exception {
		// Get the eve.yaml file:
		String path = "src/test/webapp/WEB-INF/test.yaml";
		if (path != null && !path.isEmpty()) {
			ah = AgentHost.getInstance();
			InputStream is = new FileInputStream(new File(path));
			ah.loadConfig(YamlReader.load(is).expand());
		}

		da = ah.createAgent(DomainAgent.class, DOMAIN_AGENT_ID);
	}

	@After
	public void tearDown() throws Exception {
		da.purge();
		ah.clear();
	}

	@Test
	public void testTeamUp() {

		System.out.println("Start Test");
		long start = System.currentTimeMillis();

		final int NR_TEAMS = 1;
		final int NR_TEAM_MEMBERS = 1;
		final int NR_CLIENT_GROUPS = 1;
		final int NR_CLIENTS = 100;
		final int NR_TASKS = 100;

		HashMap<String, String> teamIds = new HashMap<String, String>();
		HashMap<String, String> cgIds = new HashMap<String, String>();

		// Create x teams
		for (int t = 0; t < NR_TEAMS; t++) {

			teamIds.put("team_" + t, da.createTeamAgent("team_" + t));
			TeamAgent ta = (TeamAgent) ah.getAgent(teamIds.get("team_" + t));

			// Create team members
			for (int u = 0; u < NR_TEAM_MEMBERS; u++) {

				User user = new User("user_" + t + "_" + u,
						DigestUtils.md5Hex("askask"));
				String id = da.createTeamMemberAgent(user);
				ta.addTeamMember(id);
			}
		}

		// Create x client groups
		for (int i = 0; i < NR_CLIENT_GROUPS; i++) {
			cgIds.put("cg_" + i, da.createClientGroupAgent("cg_" + i));

			// Link a team to a client Group
			ClientGroupAgent cga = (ClientGroupAgent) ah.getAgent(cgIds
					.get("cg_" + i));
			cga.addTeam(teamIds.get("team_" + i));

			// Create clients
			for (int j = 0; j < NR_CLIENTS; j++) {

				Client client = new Client("client_" + i + "_" + j, "Client", j
						+ "");
				String id = da.createClientAgent(client);
				cga.addClient(id);

				PersonalAgent ca = (PersonalAgent) ah.getAgent(id);

				// Add tasks
				for (int t = 0; t < NR_TASKS; t++) {

					Task task = new Task((new UUID()).toString(), id);
					ca.addTask(task);
				}
			}
			System.out.println("Creating tasks took: "
					+ (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			Set<Task> tasks = cga.getTasks(false);
			System.out.println("Loading tasks took: "
					+ (System.currentTimeMillis() - start));
			Assert.assertEquals(NR_CLIENTS * NR_TASKS, tasks.size());

			tasks.clear();

			start = System.currentTimeMillis();
			tasks = cga.getTasks(true);
			System.out.println("Loading tasks in parallel took: "
					+ (System.currentTimeMillis() - start));
			Assert.assertEquals(NR_CLIENTS * NR_TASKS, tasks.size());
		}
		Assert.assertEquals(NR_TEAMS, da.getTeamIds().size());
	}

}
