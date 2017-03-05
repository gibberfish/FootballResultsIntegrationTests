package mindbadger.football.repository;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
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
import mindbadger.football.domain.SeasonDivision;
import mindbadger.football.domain.SeasonDivisionTeam;
import mindbadger.football.domain.Team;

@FixMethodOrder
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class})
public class FixtureRepositoryTest {
	private static final String INVALID_ID = "INVALID_ID";
	private static final Integer SEASON = 1852;
	private static final String DIVISION_NAME = "Fixture Test Division Name 1";
	private static final String TEAM1_NAME = "Fixture Test Team Name 1";
	private static final String TEAM2_NAME = "Fixture Test Team Name 2";
	private static final String TEAM3_NAME = "Fixture Test Team Name 3";
	private static final String TEAM4_NAME = "Fixture Test Team Name 4";

	private Season season;
	private Division division;
	private Team homeTeam1;
	private Team awayTeam1;
	private Team homeTeam2;
	private Team awayTeam2;
	
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
	public void ensureAnyRemainingTestDataIsClearedBeforeTestsRun() {
		Fixture fixture = domainObjectFactory.createFixture(season, homeTeam1, awayTeam1);
		fixture = fixtureRepository.findMatching(fixture);
		if (fixture != null) fixtureRepository.delete(fixture);

		fixture = domainObjectFactory.createFixture(season, homeTeam2, awayTeam2);
		fixture = fixtureRepository.findMatching(fixture);
		if (fixture != null) fixtureRepository.delete(fixture);

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
		
		Team team3 = domainObjectFactory.createTeam(TEAM3_NAME);
		team3 = teamRepository.findMatching(team3);
		if (team3 != null) teamRepository.delete(team3);

		Team team4 = domainObjectFactory.createTeam(TEAM4_NAME);
		team4 = teamRepository.findMatching(team4);
		if (team4 != null) teamRepository.delete(team4);

	}

	@Before
	public void setupTestData() {
		season = seasonRepository.save(domainObjectFactory.createSeason(SEASON));
		division = divisionRepository.save(domainObjectFactory.createDivision(DIVISION_NAME));
		homeTeam1 = teamRepository.save(domainObjectFactory.createTeam(TEAM1_NAME));
		awayTeam1 = teamRepository.save(domainObjectFactory.createTeam(TEAM2_NAME));
		homeTeam2 = teamRepository.save(domainObjectFactory.createTeam(TEAM3_NAME));
		awayTeam2 = teamRepository.save(domainObjectFactory.createTeam(TEAM4_NAME));
		
		SeasonDivision seasonDivision = domainObjectFactory.createSeasonDivision(season, division, 1);
		SeasonDivisionTeam seasonDivisionTeam1 = domainObjectFactory.createSeasonDivisionTeam(seasonDivision, homeTeam1);
		season.getSeasonDivisions().add(seasonDivision);
		seasonRepository.save(season);
		seasonDivision = season.getSeasonDivisions().iterator().next();
		seasonDivision.getSeasonDivisionTeams().add(seasonDivisionTeam1);
		seasonRepository.save(season);
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
		Fixture newFixture = domainObjectFactory.createFixture(season, homeTeam1, awayTeam1);
		
		// When
		Fixture fixture = fixtureRepository.findMatching(newFixture);
		
		// Then
		assertNull(fixture);
	}
	
	@Test
	public void saveShouldPersistANewObject () {
		// Given
		Fixture newFixture = domainObjectFactory.createFixture(season, homeTeam1, awayTeam1);
		
		// When
		Fixture fixture = fixtureRepository.save(newFixture);
		
		// Then
		assertNotNull(fixture);
		assertNotNull(fixture.getFixtureId());
		assertEquals(season, fixture.getSeason());
		assertEquals(homeTeam1, fixture.getHomeTeam());
		assertEquals(awayTeam1, fixture.getAwayTeam());
	}
	
	@Test
	public void findAllShouldReturnAllPersistedObjects () {
		// Given
		fixtureRepository.save(domainObjectFactory.createFixture(season, homeTeam1, awayTeam1));
		fixtureRepository.save(domainObjectFactory.createFixture(season, homeTeam2, awayTeam2));
		
		// When
		Iterable<Fixture> fixtures = fixtureRepository.findAll();
		
		// Then
		assertEquals (2, fixtures.spliterator().estimateSize());
	}
	
	@Test
	public void createOrUpdateShouldPersistANewObject () {
		// Given
		Fixture newFixture = domainObjectFactory.createFixture(season, homeTeam1, awayTeam1);
		
		// When
		Fixture fixture = fixtureRepository.createOrUpdate(newFixture);
		
		// Then
		assertNotNull(fixture);
		assertNotNull(fixture.getFixtureId());
		assertEquals(season, fixture.getSeason());
		assertEquals(homeTeam1, fixture.getHomeTeam());
		assertEquals(awayTeam1, fixture.getAwayTeam());
		assertNull(fixture.getDivision());
		assertNull(fixture.getFixtureDate());
		assertNull(fixture.getHomeGoals());
		assertNull(fixture.getAwayGoals());
	}

	@Test
	public void createOrUpdateShouldUpdateAnExistingObject () {
		// Given
		Fixture newFixture = domainObjectFactory.createFixture(season, homeTeam1, awayTeam1);
		Fixture fixture = fixtureRepository.save(newFixture);
		fixture.setDivision(division);
		Calendar fixtureDate = Calendar.getInstance();
		fixture.setFixtureDate(fixtureDate);
		Integer homeGoals = 5;
		fixture.setHomeGoals(homeGoals);
		Integer awayGoals = 3;
		fixture.setAwayGoals(awayGoals);
		
		// When
		fixture = fixtureRepository.createOrUpdate(newFixture);
		
		// Then
		assertNotNull(fixture);
		assertNotNull(fixture.getFixtureId());
		assertEquals(season, fixture.getSeason());
		assertEquals(homeTeam1, fixture.getHomeTeam());
		assertEquals(awayTeam1, fixture.getAwayTeam());
		assertEquals(fixtureDate, fixture.getFixtureDate());
		assertEquals(homeGoals, fixture.getHomeGoals());
		assertEquals(awayGoals, fixture.getAwayGoals());
	}

	@Test
	public void findMatchingShouldReturnAMatchingPersistedObject () {
		// Given
		Fixture newFixture = domainObjectFactory.createFixture(season, homeTeam1, awayTeam1);
		Fixture fixture = fixtureRepository.save(newFixture);
		
		// When
		fixture = fixtureRepository.findMatching(newFixture);
		
		// Then
		assertNotNull(fixture);
		assertNotNull(fixture.getFixtureId());
		assertEquals(season, fixture.getSeason());
		assertEquals(homeTeam1, fixture.getHomeTeam());
		assertEquals(awayTeam1, fixture.getAwayTeam());
	}
	
	@Test
	public void findOneShouldReturnAMatchingPersistedObject () {
		// Given
		Fixture newFixture = domainObjectFactory.createFixture(season, homeTeam1, awayTeam1);
		Fixture fixture = fixtureRepository.save(newFixture);
		String newFixtureId = fixture.getFixtureId();
		
		// When
		fixture = fixtureRepository.findOne(newFixtureId);
		
		// Then
		assertNotNull(fixture);
		assertNotNull(fixture.getFixtureId());
		assertEquals(season, fixture.getSeason());
		assertEquals(homeTeam1, fixture.getHomeTeam());
		assertEquals(awayTeam1, fixture.getAwayTeam());
	}
	
	@Test
	public void deleteShouldRemoveAFixture () {
		// Given
		Fixture newFixture = domainObjectFactory.createFixture(season, homeTeam1, awayTeam1);
		Fixture fixture = fixtureRepository.save(newFixture);
		String newFixtureId = fixture.getFixtureId();
		
		// When
		fixtureRepository.delete(fixture);
		
		// Then
		fixture = fixtureRepository.findOne(newFixtureId);
		assertNull(fixture);
	}
	
	@Test
	public void shouldGetFixturesWithNoFixtureDate() {
		// Given
		Calendar fixtureDate = Calendar.getInstance();
		Fixture fixture1 = domainObjectFactory.createFixture(season, homeTeam1, awayTeam1);
		fixture1.setFixtureDate(fixtureDate);
		fixture1 = fixtureRepository.save(fixture1);
		
		Fixture fixture2 = fixtureRepository.save(domainObjectFactory.createFixture(season, homeTeam2, awayTeam2));
		
		// When
		List<Fixture> fixturesWithNoFixtureDate = fixtureRepository.getFixturesWithNoFixtureDate();
		
		// Then
		assertFalse (fixturesWithNoFixtureDate.contains(fixture1));
		assertTrue (fixturesWithNoFixtureDate.contains(fixture2));
	}
	
	@Test
	public void shouldGetFixturesForTeamInDivisionInSeason() {
		// Given
		Calendar fixtureDate = Calendar.getInstance();
		Fixture fixture1 = domainObjectFactory.createFixture(season, homeTeam1, awayTeam1);
		fixture1.setFixtureDate(fixtureDate);
		fixture1 = fixtureRepository.save(fixture1);
		
		Fixture fixture2 = fixtureRepository.save(domainObjectFactory.createFixture(season, homeTeam2, awayTeam2));
		
		SeasonDivision seasonDivision = season.getSeasonDivisions().iterator().next();
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println(seasonDivision.getSeason());
		System.out.println(seasonDivision.getDivision());
		System.out.println(seasonDivision.getSeasonDivisionTeams().iterator().next().getTeam());
		
		// When
		List<Fixture> fixturesForTeam1 = fixtureRepository.getFixturesForTeamInDivisionInSeason(seasonDivision, homeTeam1);
		
		// Then
		assertTrue (fixturesForTeam1.contains(fixture1));
		assertFalse (fixturesForTeam1.contains(fixture2));
	}

}
