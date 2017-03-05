package mindbadger.football.repository;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mindbadger.TestApplication;
import mindbadger.football.domain.TeamMapping;
import mindbadger.football.domain.DomainObjectFactory;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class})
public class TeamMappingRepositoryTest {
	private static final String DIALECT = "mydialect";
	private static final String INVALID_DIALECT = "INVALID_ID";
	private static final Integer SOURCE_ID1 = 10;
	private static final Integer FRA_ID1 = 20;
	private static final Integer SOURCE_ID2 = 11;
	private static final Integer FRA_ID2 = 21;

	@Autowired
	private TeamMappingRepository teamMappingRepository;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@After
	@Before
	public void ensureAnyRemainingTestDataIsClearedBeforeTestsRun() {
		TeamMapping newTeamMapping = domainObjectFactory.createTeamMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		TeamMapping teamMapping = teamMappingRepository.findOne(newTeamMapping);
		if (teamMapping != null) teamMappingRepository.delete(teamMapping);
		
		newTeamMapping = domainObjectFactory.createTeamMapping(DIALECT, SOURCE_ID2, FRA_ID2);
		teamMapping = teamMappingRepository.findOne(newTeamMapping);
		if (teamMapping != null) teamMappingRepository.delete(teamMapping);
	}
	
	@Test
	public void findOneShouldReturnNullIfANullIdIsPassedIn () {
		// When
		TeamMapping teamMapping = teamMappingRepository.findOne(null);
		
		// Then
		assertNull(teamMapping);
	}
	
	@Test
	public void findOneShouldReturnNullIfANonExistentObjectIsPassedIn () {
		// When
		TeamMapping invalidDivsionMapping = domainObjectFactory.createTeamMapping(INVALID_DIALECT, 0, 0);
		TeamMapping teamMapping = teamMappingRepository.findOne(invalidDivsionMapping);
		
		// Then
		assertNull(teamMapping);
	}
	
	@Test
	public void saveShouldPersistANewObject () {
		// Given
		TeamMapping newTeamMapping = domainObjectFactory.createTeamMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		
		// When
		TeamMapping teamMapping = teamMappingRepository.save(newTeamMapping);
		
		// Then
		assertNotNull(teamMapping);
		assertEquals(DIALECT, teamMapping.getDialect());
		assertEquals (SOURCE_ID1, teamMapping.getSourceId());
		assertEquals (FRA_ID1, teamMapping.getFraId());
	}
	
	@Test
	public void findAllShouldReturnAllPersistedObjects () {
		// Given
		teamMappingRepository.save(domainObjectFactory.createTeamMapping(DIALECT, SOURCE_ID1, FRA_ID1));
		teamMappingRepository.save(domainObjectFactory.createTeamMapping(DIALECT, SOURCE_ID2, FRA_ID2));
		
		// When
		Iterable<TeamMapping> teamMappings = teamMappingRepository.findAll();
		
		// Then
		assertTrue (teamMappings.spliterator().estimateSize() >= 2);
	}
	
	@Test
	public void createOrUpdateShouldThrowAnException () {
		// Given
		TeamMapping newTeamMapping = domainObjectFactory.createTeamMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		
		try {
			// When
			teamMappingRepository.createOrUpdate(newTeamMapping);
			fail ("Should have thrown an exception here");
		} catch (RuntimeException e) {
			assertEquals ("This method is not implemented for TeamMappings", e.getMessage());
		}
	}
	
	@Test
	public void findMatchingShouldReturnAMatchingPersistedObject () {
		// Given
		TeamMapping newTeamMapping = domainObjectFactory.createTeamMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		TeamMapping teamMapping = teamMappingRepository.save(newTeamMapping);
		
		try {
			// When
			teamMappingRepository.findMatching(teamMapping);
			fail ("Should have thrown an exception here");
		} catch (RuntimeException e) {
			assertEquals ("This method is not implemented for TeamMappings", e.getMessage());
		}
	}
	
	@Test
	public void findOneShouldReturnAMatchingPersistedObject () {
		// Given
		TeamMapping newTeamMapping = domainObjectFactory.createTeamMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		TeamMapping teamMapping = teamMappingRepository.save(newTeamMapping);
		
		// When
		teamMapping = teamMappingRepository.findOne(newTeamMapping);
		
		// Then
		assertNotNull(teamMapping);
		assertEquals(DIALECT, teamMapping.getDialect());
		assertEquals (SOURCE_ID1, teamMapping.getSourceId());
		assertEquals (FRA_ID1, teamMapping.getFraId());
	}
	
	@Test
	public void deleteShouldRemoveATeamMapping () {
		// Given
		TeamMapping newTeamMapping = domainObjectFactory.createTeamMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		TeamMapping teamMapping = teamMappingRepository.save(newTeamMapping);
		
		// When
		teamMappingRepository.delete(teamMapping);
		
		// Then
		teamMapping = teamMappingRepository.findOne(newTeamMapping);
		assertNull(teamMapping);
	}
}
