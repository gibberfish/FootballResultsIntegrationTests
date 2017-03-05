package mindbadger.football.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
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
	private static final String NEW_TEAM1_NAME = "New Team 1";
	private static final String NEW_TEAM2_NAME = "New Team 2";
	private static final String INVALID_ID = "INVALID_ID";

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@After
	@Before
	public void ensureAnyRemainingTestDataIsClearedBeforeTestsRun() {
		Team team = domainObjectFactory.createTeam(NEW_TEAM1_NAME);
		team = teamRepository.findMatching(team);
		if (team != null) teamRepository.delete(team);
	}

	@Test
	public void findOneShouldReturnNullIfANullIdIsPassedIn () {
		// When
		Team team = teamRepository.findOne(null);
		
		// Then
		assertNull(team);
	}
	
	@Test
	public void findOneShouldReturnNullIfANonExistentIdIsPassedIn () {
		// When
		Team team = teamRepository.findOne(INVALID_ID);
		
		// Then
		assertNull(team);
	}
	
	@Test
	public void findMatchingShouldReturnNullIfANonPersistedObjectIsPassedIn () {
		// Given
		Team newTeam = domainObjectFactory.createTeam(NEW_TEAM1_NAME);
		
		// When
		Team team = teamRepository.findMatching(newTeam);
		
		// Then
		assertNull(team);
	}
	
	@Test
	public void saveShouldPersistANewObject () {
		// Given
		Team newTeam = domainObjectFactory.createTeam(NEW_TEAM1_NAME);
		
		// When
		Team team = teamRepository.save(newTeam);
		
		// Then
		assertNotNull(team);
		assertNotNull(team.getTeamId());
		assertEquals (NEW_TEAM1_NAME, team.getTeamName());
	}
	
	@Test
	public void findAllShouldReturnAllPersistedObjects () {
		// Given
		teamRepository.save(domainObjectFactory.createTeam(NEW_TEAM1_NAME));
		teamRepository.save(domainObjectFactory.createTeam(NEW_TEAM2_NAME));
		
		// When
		Iterable<Team> teams = teamRepository.findAll();
		
		// Then
		assertEquals (2, teams.spliterator().estimateSize());
	}
	
	@Test
	public void createOrUpdateShouldPersistANewObject () {
		// Given
		Team newTeam = domainObjectFactory.createTeam(NEW_TEAM1_NAME);
		
		// When
		Team team = teamRepository.createOrUpdate(newTeam);
		
		// Then
		assertNotNull(team);
		assertNotNull(team.getTeamId());
		assertEquals (NEW_TEAM1_NAME, team.getTeamName());
	}
	
	@Test
	public void findMatchingShouldReturnAMatchingPersistedObject () {
		// Given
		Team newTeam = domainObjectFactory.createTeam(NEW_TEAM1_NAME);
		Team team = teamRepository.save(newTeam);
		
		// When
		team = teamRepository.findMatching(newTeam);
		
		// Then
		assertEquals (NEW_TEAM1_NAME, team.getTeamName());
		assertNotNull(team.getTeamId());
	}
	
	@Test
	public void findOneShouldReturnAMatchingPersistedObject () {
		// Given
		Team newTeam = domainObjectFactory.createTeam(NEW_TEAM1_NAME);
		Team team = teamRepository.save(newTeam);
		String newTeamId = team.getTeamId();
		
		// When
		team = teamRepository.findOne(newTeamId);
		
		// Then
		assertNotNull(team);
		assertEquals (NEW_TEAM1_NAME, team.getTeamName());
		assertEquals (newTeamId, team.getTeamId());
	}
	
	@Test
	public void deleteShouldRemoveATeam () {
		// Given
		Team newTeam = domainObjectFactory.createTeam(NEW_TEAM1_NAME);
		Team team = teamRepository.save(newTeam);
		String newTeamId = team.getTeamId();
		
		// When
		teamRepository.delete(team);
		
		// Then
		team = teamRepository.findOne(newTeamId);
		assertNull(team);
	}
}
