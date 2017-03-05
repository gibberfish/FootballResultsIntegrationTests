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
import mindbadger.football.domain.Division;
import mindbadger.football.domain.DomainObjectFactory;
import mindbadger.football.domain.Fixture;
import mindbadger.football.domain.Season;
import mindbadger.football.domain.Team;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class})
public class FixtureRepositoryTest {
	private static final String INVALID_ID = "INVALID_ID";
	private static final Integer SEASON = 1850;
	private static final String DIVISION_NAME = "Fixture Test Division Name 1";
	private static final String TEAM1_NAME = "Fixture Test Team Name 1";
	private static final String TEAM2_NAME = "Fixture Test Team Name 2";

	private Season season;
	private Division division;
	private Team homeTeam;
	private Team awayTeam;
	
	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private FixtureRepository fixtureRepository;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@After
	@Before
	public void ensureAnyRemainingTestDataIsClearedBeforeTestsRun() {
		Season season = seasonRepository.findOne(SEASON);
		if (season != null) seasonRepository.delete(season);
		
		Division division = domainObjectFactory.createDivision(DIVISION_NAME);
		division = divisionRepository.findMatching(division);
		if (division != null) divisionRepository.delete(division);

		Team team1 = domainObjectFactory.createTeam(TEAM1_NAME);
		team1 = teamRepository.findMatching(team1);
		if (team1 != null) teamRepository.delete(team1);

		Team team2 = domainObjectFactory.createTeam(TEAM2_NAME);
		team2 = teamRepository.findMatching(team2);
		if (team2 != null) teamRepository.delete(team2);

//		Fixture fixture = domainObjectFactory.createFixture(NEW_TEAM1_NAME);
//		fixture = fixtureRepository.findMatching(fixture);
//		if (fixture != null) fixtureRepository.delete(fixture);
	}

	@Before
	public void setupTestData() {
		season = seasonRepository.save(domainObjectFactory.createSeason(SEASON));
		division = divisionRepository.save(domainObjectFactory.createDivision(DIVISION_NAME));
		homeTeam = teamRepository.save(domainObjectFactory.createTeam(TEAM1_NAME));
		awayTeam = teamRepository.save(domainObjectFactory.createTeam(TEAM2_NAME));
	}
	
	@Test
	public void findOneShouldReturnNullIfANullIdIsPassedIn () {
		// When
		Fixture fixture = fixtureRepository.findOne(null);
		
		// Then
		assertNull(fixture);
	}
	
	@Test
	public void findOneShouldReturnNullIfANonExistentIdIsPassedIn () {
		// When
		Fixture fixture = fixtureRepository.findOne(INVALID_ID);
		
		// Then
		assertNull(fixture);
	}
	
	@Test
	public void findMatchingShouldReturnNullIfANonPersistedObjectIsPassedIn () {
		// Given
		Fixture newFixture = domainObjectFactory.createFixture(season, homeTeam, awayTeam);
		
		// When
		Fixture fixture = fixtureRepository.findMatching(newFixture);
		
		// Then
		assertNull(fixture);
	}
//	
//	@Test
//	public void saveShouldPersistANewObject () {
//		// Given
//		Fixture newFixture = domainObjectFactory.createFixture(NEW_TEAM1_NAME);
//		
//		// When
//		Fixture fixture = fixtureRepository.save(newFixture);
//		
//		// Then
//		assertNotNull(fixture);
//		assertNotNull(fixture.getFixtureId());
//		assertEquals (NEW_TEAM1_NAME, fixture.getFixtureName());
//	}
//	
//	@Test
//	public void findAllShouldReturnAllPersistedObjects () {
//		// Given
//		fixtureRepository.save(domainObjectFactory.createFixture(NEW_TEAM1_NAME));
//		fixtureRepository.save(domainObjectFactory.createFixture(NEW_TEAM2_NAME));
//		
//		// When
//		Iterable<Fixture> fixtures = fixtureRepository.findAll();
//		
//		// Then
//		assertEquals (2, fixtures.spliterator().estimateSize());
//	}
//	
//	@Test
//	public void createOrUpdateShouldPersistANewObject () {
//		// Given
//		Fixture newFixture = domainObjectFactory.createFixture(NEW_TEAM1_NAME);
//		
//		// When
//		Fixture fixture = fixtureRepository.createOrUpdate(newFixture);
//		
//		// Then
//		assertNotNull(fixture);
//		assertNotNull(fixture.getFixtureId());
//		assertEquals (NEW_TEAM1_NAME, fixture.getFixtureName());
//	}
//	
//	@Test
//	public void findMatchingShouldReturnAMatchingPersistedObject () {
//		// Given
//		Fixture newFixture = domainObjectFactory.createFixture(NEW_TEAM1_NAME);
//		Fixture fixture = fixtureRepository.save(newFixture);
//		
//		// When
//		fixture = fixtureRepository.findMatching(newFixture);
//		
//		// Then
//		assertEquals (NEW_TEAM1_NAME, fixture.getFixtureName());
//		assertNotNull(fixture.getFixtureId());
//	}
//	
//	@Test
//	public void findOneShouldReturnAMatchingPersistedObject () {
//		// Given
//		Fixture newFixture = domainObjectFactory.createFixture(NEW_TEAM1_NAME);
//		Fixture fixture = fixtureRepository.save(newFixture);
//		String newFixtureId = fixture.getFixtureId();
//		
//		// When
//		fixture = fixtureRepository.findOne(newFixtureId);
//		
//		// Then
//		assertNotNull(fixture);
//		assertEquals (NEW_TEAM1_NAME, fixture.getFixtureName());
//		assertEquals (newFixtureId, fixture.getFixtureId());
//	}
//	
//	@Test
//	public void deleteShouldRemoveAFixture () {
//		// Given
//		Fixture newFixture = domainObjectFactory.createFixture(NEW_TEAM1_NAME);
//		Fixture fixture = fixtureRepository.save(newFixture);
//		String newFixtureId = fixture.getFixtureId();
//		
//		// When
//		fixtureRepository.delete(fixture);
//		
//		// Then
//		fixture = fixtureRepository.findOne(newFixtureId);
//		assertNull(fixture);
//	}
}
