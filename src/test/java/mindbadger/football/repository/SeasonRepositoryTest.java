package mindbadger.football.repository;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mindbadger.TestApplication;
import mindbadger.football.domain.Division;
import mindbadger.football.domain.DomainObjectFactory;
import mindbadger.football.domain.Season;
import mindbadger.football.domain.SeasonDivision;
import mindbadger.football.domain.SeasonDivisionTeam;
import mindbadger.football.domain.Team;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class})
public class SeasonRepositoryTest {
	private static final Integer NEW_SEASON_NUMBER = 1850;
	private static final Integer SECOND_SEASON_NUMBER = 1851;
	private static final Integer INVALID_ID = -1;
	private static final String TEAM_NAME_1 = "New Team 1";
	private static final String TEAM_NAME_2 = "New Team 2";
	private static final String DIV_NAME_1 = "New Division 1";
	private static final String DIV_NAME_2 = "New Division 2";

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@After
	@Before
	public void ensureAnyRemainingTestDataIsClearedBeforeTestsRun() {
		Season season = seasonRepository.findOne(1850);
		if (season != null) seasonRepository.delete(season);
		
		Division division = domainObjectFactory.createDivision(DIV_NAME_1);
		division = divisionRepository.findMatching(division);
		if (division != null ) divisionRepository.delete(division);
		
		division = domainObjectFactory.createDivision(DIV_NAME_2);
		division = divisionRepository.findMatching(division);
		if (division != null ) divisionRepository.delete(division);

		Team team = domainObjectFactory.createTeam(TEAM_NAME_1);
		team = teamRepository.findMatching(team);
		if (team != null) teamRepository.delete(team);

		team = domainObjectFactory.createTeam(TEAM_NAME_2);
		team = teamRepository.findMatching(team);
		if (team != null) teamRepository.delete(team);
	}

	@Test
	public void findOneShouldReturnNullIfANullIdIsPassedIn () {
		Season season = seasonRepository.findOne(null);
		assertNull(season);
	}

	@Test
	public void findOneShouldReturnNullIfANonExistentIdIsPassedIn () {
		Season season = seasonRepository.findOne(INVALID_ID);
		assertNull(season);
	}

	@Test
	public void findMatchingShouldReturnNullIfANonPersistedObjectIsPassedIn () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.findMatching(newSeason);
		assertNull(season);
	}

	@Test
	public void saveShouldPersistANewObject () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.save(newSeason);
		assertNotNull(season);
		assertEquals (NEW_SEASON_NUMBER, season.getSeasonNumber());
		assertEquals (0, season.getSeasonDivisions().size());		
	}

	@Test
	public void findAllShouldReturnAllPersistedObjects () {
		seasonRepository.save(domainObjectFactory.createSeason(NEW_SEASON_NUMBER));
		seasonRepository.save(domainObjectFactory.createSeason(SECOND_SEASON_NUMBER));

		Iterable<Season> seasons = seasonRepository.findAll();
		
		assertEquals (2, seasons.spliterator().estimateSize());		
	}

	//TODO Currently this method fails because the ID and the 
	@Ignore
	@Test
	public void createOrUpdateShouldPersistANewObject () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.createOrUpdate(newSeason);
		assertNotNull(season);
		assertEquals (NEW_SEASON_NUMBER, season.getSeasonNumber());
		assertEquals (0, season.getSeasonDivisions().size());		
	}

	@Test
	public void findMatchingShouldReturnAMatchingPersistedObject () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.save(newSeason);

		season = seasonRepository.findMatching(newSeason);
		assertEquals (NEW_SEASON_NUMBER, season.getSeasonNumber());		
		assertEquals (0, season.getSeasonDivisions().size());
	}

	@Test
	public void findOneShouldReturnAMatchingPersistedObject () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.save(newSeason);
		Integer newSeasonId = season.getSeasonNumber();

		season = seasonRepository.findOne(newSeasonId);
		assertEquals (NEW_SEASON_NUMBER, season.getSeasonNumber());
		assertEquals (0, season.getSeasonDivisions().size());		
	}

	@Test
	public void shouldAddDivisionToASeason () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.save(newSeason);
		Integer newSeasonId = season.getSeasonNumber();

		Division division1 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_1));
		SeasonDivision seasonDivision1 = domainObjectFactory.createSeasonDivision(season, division1, 1);
		season.getSeasonDivisions().add(seasonDivision1);
		season = seasonRepository.save(season);
		
		assertEquals (1, season.getSeasonDivisions().size());
		
		// Re-retrieve the season and ensure we still have a division
		season = seasonRepository.findOne(newSeasonId);
		assertEquals (1, season.getSeasonDivisions().size());
	}

	@Test
	public void shouldAddMultipleDivisionsToASeason () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.save(newSeason);
		Integer newSeasonId = season.getSeasonNumber();

		Division division1 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_1));
		SeasonDivision seasonDivision1 = domainObjectFactory.createSeasonDivision(season, division1, 1);
		season.getSeasonDivisions().add(seasonDivision1);
		season = seasonRepository.save(season);
		
		Division division2 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_2));
		SeasonDivision seasonDivision2 = domainObjectFactory.createSeasonDivision(season, division2, 2);
		season.getSeasonDivisions().add(seasonDivision2);
		season = seasonRepository.save(season);
		
		assertEquals (2, season.getSeasonDivisions().size());
		
		// Re-retrieve the season and ensure we still have a division
		season = seasonRepository.findOne(newSeasonId);
		assertEquals (2, season.getSeasonDivisions().size());
	}

	@Test
	public void shouldAddTeamToDivisionInASeason () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.save(newSeason);
		Integer newSeasonId = season.getSeasonNumber();

		Division division1 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_1));
		SeasonDivision seasonDivision1 = domainObjectFactory.createSeasonDivision(season, division1, 1);
		season.getSeasonDivisions().add(seasonDivision1);
		season = seasonRepository.save(season);
		
		seasonDivision1 = season.getSeasonDivisions().iterator().next();

		Team team1 = teamRepository.save(domainObjectFactory.createTeam(TEAM_NAME_1));
		SeasonDivisionTeam seasonDivisionTeam1 = domainObjectFactory.createSeasonDivisionTeam(seasonDivision1, team1);
		seasonDivision1.getSeasonDivisionTeams().add(seasonDivisionTeam1);
		season = seasonRepository.save(season);
		
		assertEquals (1, seasonDivision1.getSeasonDivisionTeams().size());
		
		season = seasonRepository.findOne(newSeasonId);
		seasonDivision1 = season.getSeasonDivisions().iterator().next();
		
		assertEquals (1, seasonDivision1.getSeasonDivisionTeams().size());
	}

	@Test
	public void shouldAddAnotherTeamToADifferentDivisionInASeason () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.save(newSeason);

		Division division1 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_1));
		Division division2 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_2));
		
		SeasonDivision seasonDivision1 = domainObjectFactory.createSeasonDivision(season, division1, 1);
		SeasonDivision seasonDivision2 = domainObjectFactory.createSeasonDivision(season, division2, 2);
		
		season.getSeasonDivisions().add(seasonDivision1);
		season.getSeasonDivisions().add(seasonDivision2);
		season = seasonRepository.save(season);
		
		Team team1 = teamRepository.save(domainObjectFactory.createTeam(TEAM_NAME_1));
		Team team2 = teamRepository.save(domainObjectFactory.createTeam(TEAM_NAME_2));

		List<SeasonDivision> seasonDivisionList = new ArrayList<SeasonDivision> (season.getSeasonDivisions());
		seasonDivision1 = seasonDivisionList.get(0);
		seasonDivision2 = seasonDivisionList.get(1);
		
		SeasonDivisionTeam seasonDivisionTeam1 = domainObjectFactory.createSeasonDivisionTeam(seasonDivision1, team1);
		SeasonDivisionTeam seasonDivisionTeam2 = domainObjectFactory.createSeasonDivisionTeam(seasonDivision2, team2);
		
		seasonDivision1.getSeasonDivisionTeams().add(seasonDivisionTeam1);
		seasonDivision2.getSeasonDivisionTeams().add(seasonDivisionTeam2);
		season = seasonRepository.save(season);

		seasonDivisionList = new ArrayList<SeasonDivision> (season.getSeasonDivisions());
		seasonDivision1 = seasonDivisionList.get(0);
		seasonDivision2 = seasonDivisionList.get(1);

		assertEquals (1, seasonDivision1.getSeasonDivisionTeams().size());
		assertEquals (1, seasonDivision2.getSeasonDivisionTeams().size());
	}

	@Test
	public void shouldRemoveATeamFromADivisionInASeason () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.save(newSeason);

		Division division1 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_1));
		SeasonDivision seasonDivision1 = domainObjectFactory.createSeasonDivision(season, division1, 1);
		season.getSeasonDivisions().add(seasonDivision1);
		season = seasonRepository.save(season);
		
		Team team1 = teamRepository.save(domainObjectFactory.createTeam(TEAM_NAME_1));

		List<SeasonDivision> seasonDivisionList = new ArrayList<SeasonDivision> (season.getSeasonDivisions());
		seasonDivision1 = season.getSeasonDivisions().iterator().next();
		
		SeasonDivisionTeam seasonDivisionTeam1 = domainObjectFactory.createSeasonDivisionTeam(seasonDivision1, team1);
		seasonDivision1.getSeasonDivisionTeams().add(seasonDivisionTeam1);
		season = seasonRepository.save(season);

		seasonDivision1 = season.getSeasonDivisions().iterator().next();
		seasonDivisionTeam1 = seasonDivision1.getSeasonDivisionTeams().iterator().next();

		seasonDivision1.getSeasonDivisionTeams().remove(seasonDivisionTeam1);
		season = seasonRepository.save(season);
		
		seasonDivision1 = season.getSeasonDivisions().iterator().next();
		
		assertEquals (0, seasonDivision1.getSeasonDivisionTeams().size());
	}

	@Test
	public void shouldMoveATeamToADifferentDivisionInASeason () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.save(newSeason);

		Division division1 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_1));
		Division division2 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_2));
		
		SeasonDivision seasonDivision1 = domainObjectFactory.createSeasonDivision(season, division1, 1);
		SeasonDivision seasonDivision2 = domainObjectFactory.createSeasonDivision(season, division2, 2);
		
		season.getSeasonDivisions().add(seasonDivision1);
		season.getSeasonDivisions().add(seasonDivision2);
		season = seasonRepository.save(season);
		
		Team team1 = teamRepository.save(domainObjectFactory.createTeam(TEAM_NAME_1));
		Team team2 = teamRepository.save(domainObjectFactory.createTeam(TEAM_NAME_2));

		List<SeasonDivision> seasonDivisionList = new ArrayList<SeasonDivision> (season.getSeasonDivisions());
		seasonDivision1 = seasonDivisionList.get(0);
		seasonDivision2 = seasonDivisionList.get(1);
		
		SeasonDivisionTeam seasonDivisionTeam1 = domainObjectFactory.createSeasonDivisionTeam(seasonDivision1, team1);
		SeasonDivisionTeam seasonDivisionTeam2 = domainObjectFactory.createSeasonDivisionTeam(seasonDivision2, team2);
		
		seasonDivision1.getSeasonDivisionTeams().add(seasonDivisionTeam1);
		seasonDivision2.getSeasonDivisionTeams().add(seasonDivisionTeam2);
		season = seasonRepository.save(season);

		seasonDivisionList = new ArrayList<SeasonDivision> (season.getSeasonDivisions());
		seasonDivision1 = seasonDivisionList.get(0);
		seasonDivision2 = seasonDivisionList.get(1);
		seasonDivisionTeam1 = seasonDivision1.getSeasonDivisionTeams().iterator().next();
		seasonDivisionTeam2 = seasonDivision2.getSeasonDivisionTeams().iterator().next();
		
		seasonDivision2.getSeasonDivisionTeams().remove(seasonDivisionTeam2);
		seasonDivision1.getSeasonDivisionTeams().add(seasonDivisionTeam2);
		season = seasonRepository.save(season);

		seasonDivisionList = new ArrayList<SeasonDivision> (season.getSeasonDivisions());
		seasonDivision1 = seasonDivisionList.get(0);
		seasonDivision2 = seasonDivisionList.get(1);

		assertEquals (2, seasonDivision1.getSeasonDivisionTeams().size());
		assertEquals (0, seasonDivision2.getSeasonDivisionTeams().size());
	}
	
	@Test
	public void shouldNavigateThroughSeasonsAndDivisions () {
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NUMBER);
		Season season = seasonRepository.save(newSeason);

		Division division1 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_1));
		Division division2 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_2));
		
		SeasonDivision seasonDivision1 = domainObjectFactory.createSeasonDivision(season, division1, 1);
		SeasonDivision seasonDivision2 = domainObjectFactory.createSeasonDivision(season, division2, 2);
		
		season.getSeasonDivisions().add(seasonDivision1);
		season.getSeasonDivisions().add(seasonDivision2);
		season = seasonRepository.save(season);
		
		Team team1 = teamRepository.save(domainObjectFactory.createTeam(TEAM_NAME_1));
		Team team2 = teamRepository.save(domainObjectFactory.createTeam(TEAM_NAME_2));

		List<SeasonDivision> seasonDivisionList = new ArrayList<SeasonDivision> (season.getSeasonDivisions());
		seasonDivision1 = seasonDivisionList.get(0);
		seasonDivision2 = seasonDivisionList.get(1);
		
		SeasonDivisionTeam seasonDivisionTeam1 = domainObjectFactory.createSeasonDivisionTeam(seasonDivision1, team1);
		SeasonDivisionTeam seasonDivisionTeam2 = domainObjectFactory.createSeasonDivisionTeam(seasonDivision2, team2);
		
		seasonDivision1.getSeasonDivisionTeams().add(seasonDivisionTeam1);
		seasonDivision2.getSeasonDivisionTeams().add(seasonDivisionTeam2);
		season = seasonRepository.save(season);
		
		seasonDivision1 = seasonRepository.getSeasonDivision(season, division1);
		seasonDivision2 = seasonRepository.getSeasonDivision(season, division2);
		assertEquals (DIV_NAME_1, seasonDivision1.getDivision().getDivisionName());
		assertEquals (DIV_NAME_2, seasonDivision2.getDivision().getDivisionName());

		seasonDivisionTeam1 = seasonRepository.getSeasonDivisionTeam(seasonDivision1, team1);
		seasonDivisionTeam2 = seasonRepository.getSeasonDivisionTeam(seasonDivision2, team2);
		team1 = seasonDivisionTeam1.getTeam();
		team2 = seasonDivisionTeam2.getTeam();
		assertEquals (TEAM_NAME_1, team1.getTeamName());
		assertEquals (TEAM_NAME_2, team2.getTeamName());
	}
}
