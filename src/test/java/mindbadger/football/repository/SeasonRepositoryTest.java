package mindbadger.football.repository;

import static org.junit.Assert.*;

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
import mindbadger.football.domain.Season;
import mindbadger.football.domain.SeasonDivision;
import mindbadger.football.domain.SeasonDivisionTeam;
import mindbadger.football.domain.Team;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class})
public class SeasonRepositoryTest {
	private static final Integer NEW_SEASON_NAME = 1850;
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

	@Before
	public void init() {
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
	public void testSeasonRepository() {
		// Attempt to find a season with a null ID
		Season season = seasonRepository.findOne(null);
		assertNull(season);

		// Attempt to find a season with an ID that doesn't exist
		season = seasonRepository.findOne(INVALID_ID);
		assertNull(season);
		
		// Attempt to find a season matching a non-persisted domain object
		Season newSeason = domainObjectFactory.createSeason(NEW_SEASON_NAME);
		season = seasonRepository.findMatching(newSeason);
		assertNull(season);

		// Save the new domain object
		season = seasonRepository.save(newSeason);
		assertNotNull(season);
		assertEquals (NEW_SEASON_NAME, season.getSeasonNumber());

		// Attempt to find a season matching the now-persisted domain object
		season = seasonRepository.findMatching(newSeason);
		assertEquals (NEW_SEASON_NAME, season.getSeasonNumber());
		
		// Attempt to find a season using the ID of the newly persisted domain object
		Integer newSeasonId = season.getSeasonNumber();
		season = seasonRepository.findOne(newSeasonId);
		assertNotNull(season);
		assertEquals (NEW_SEASON_NAME, season.getSeasonNumber());
		
		// Check that there are no divisions in the season
		assertEquals (0, season.getSeasonDivisions().size());
		
		// Add a division to the season
		Division division1 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_1));
		SeasonDivision seasonDivision1 = domainObjectFactory.createSeasonDivision(season, division1, 1);
		season.getSeasonDivisions().add(seasonDivision1);
		season = seasonRepository.save(season);
		
		assertEquals (1, season.getSeasonDivisions().size());
		
		// Re-retrieve the season and ensure we still have a division
		season = seasonRepository.findOne(newSeasonId);
		assertEquals (1, season.getSeasonDivisions().size());

		// Add another division to the season
		Division division2 = divisionRepository.save(domainObjectFactory.createDivision(DIV_NAME_2));
		SeasonDivision seasonDivision2 = domainObjectFactory.createSeasonDivision(season, division2, 2);
		season.getSeasonDivisions().add(seasonDivision2);
		season = seasonRepository.save(season);
		
		assertEquals (2, season.getSeasonDivisions().size());
		
		// Re-retrieve the season and ensure we still have a division
		season = seasonRepository.findOne(newSeasonId);
		assertEquals (2, season.getSeasonDivisions().size());

		// Re-retrieve the first season division from the season
		seasonDivision1 = null;
		for (SeasonDivision seasonDivision : season.getSeasonDivisions()) {
			if (seasonDivision.getDivisionPosition() == 1) {
				seasonDivision1 = seasonDivision;
			}
		}
		
		// Add a team to the first division
		Team team1 = teamRepository.save(domainObjectFactory.createTeam(TEAM_NAME_1));
		SeasonDivisionTeam seasonDivisionTeam1 = domainObjectFactory.createSeasonDivisionTeam(seasonDivision1, team1);
		seasonDivision1.getSeasonDivisionTeams().add(seasonDivisionTeam1);
		season = seasonRepository.save(season);
		
		assertEquals (1, seasonDivision1.getSeasonDivisionTeams().size());
		
		// Re-retrieve the season and ensure we still have a team in the division
		season = seasonRepository.findOne(newSeasonId);
		
		for (SeasonDivision seasonDivision : season.getSeasonDivisions()) {
			if (seasonDivision.getDivisionPosition() == 1) {
				assertEquals (1, seasonDivision.getSeasonDivisionTeams().size());
			} else if (seasonDivision.getDivisionPosition() == 2) {
				assertEquals (0, seasonDivision.getSeasonDivisionTeams().size());
			} else {
				fail("Should not have a season division with a position other than 1 or 2");
			}
		}
		
		// Delete the domain object and check it can no longer be retrieved
		seasonRepository.delete(season);
		season = seasonRepository.findOne(newSeasonId);
		assertNull(season);
		
		divisionRepository.delete(division1);
		divisionRepository.delete(division2);
	}
}
