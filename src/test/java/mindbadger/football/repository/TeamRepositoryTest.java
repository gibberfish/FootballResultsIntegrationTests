package mindbadger.football.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mindbadger.TestApplication;
import mindbadger.football.domain.DomainObjectFactory;
import mindbadger.football.domain.Team;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class})
public class TeamRepositoryTest {
	private static final String NEW_TEAM_NAME = "New Team";
	private static final String INVALID_ID = "INVALID_ID";

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@Before
	public void init() {
		Team team = domainObjectFactory.createTeam(NEW_TEAM_NAME);
		team = teamRepository.findMatching(team);
		if (team != null) teamRepository.delete(team);
	}

	@Test
	public void testTeamRepository() {
		// Attempt to find a team with a null ID
		Team team = teamRepository.findOne(null);
		assertNull(team);

		// Attempt to find a team with an ID that doesn't exist
		team = teamRepository.findOne(INVALID_ID);
		assertNull(team);
		
		// Attempt to find a team matching a non-persisted domain object
		Team newTeam = domainObjectFactory.createTeam(NEW_TEAM_NAME);
		team = teamRepository.findMatching(newTeam);
		assertNull(team);

		// Save the new domain object
		team = teamRepository.save(newTeam);
		assertNotNull(team);
		assertNotNull(team.getTeamId());
		assertEquals (NEW_TEAM_NAME, team.getTeamName());

		// Attempt to find a team matching the now-persisted domain object
		team = teamRepository.findMatching(newTeam);
		assertEquals (NEW_TEAM_NAME, team.getTeamName());
		assertNotNull(team.getTeamId());
		
		// Attempt to find a team using the ID of the newly persisted domain object
		String newTeamId = team.getTeamId();
		team = teamRepository.findOne(newTeamId);
		assertNotNull(team);
		assertEquals (NEW_TEAM_NAME, team.getTeamName());
		assertEquals (newTeamId, team.getTeamId());
		
		// Delete the domain object and check it can no longer be retrieved
		teamRepository.delete(team);
		team = teamRepository.findOne(newTeamId);
		assertNull(team);
	}
}
