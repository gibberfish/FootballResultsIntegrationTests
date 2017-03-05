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
import mindbadger.football.domain.Division;
import mindbadger.football.domain.DomainObjectFactory;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class})
public class DivisionRepositoryTest {
	private static final String NEW_DIVISION1_NAME = "New Division 1";
	private static final String NEW_DIVISION2_NAME = "New Division 2";
	private static final String INVALID_ID = "INVALID_ID";

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@After
	@Before
	public void ensureAnyRemainingTestDataIsClearedBeforeTestsRun() {
		Division division = domainObjectFactory.createDivision(NEW_DIVISION1_NAME);
		division = divisionRepository.findMatching(division);
		if (division != null ) divisionRepository.delete(division);
	}
	
	@Test
	public void findOneShouldReturnNullIfANullIdIsPassedIn () {
		// When
		Division division = divisionRepository.findOne(null);
		
		// Then
		assertNull(division);
	}
	
	@Test
	public void findOneShouldReturnNullIfANonExistentIdIsPassedIn () {
		// When
		Division division = divisionRepository.findOne(INVALID_ID);
		
		// Then
		assertNull(division);
	}
	
	@Test
	public void findMatchingShouldReturnNullIfANonPersistedObjectIsPassedIn () {
		// Given
		Division newDivision = domainObjectFactory.createDivision(NEW_DIVISION1_NAME);
		
		// When
		Division division = divisionRepository.findMatching(newDivision);
		
		// Then
		assertNull(division);
	}
	
	@Test
	public void saveShouldPersistANewObject () {
		// Given
		Division newDivision = domainObjectFactory.createDivision(NEW_DIVISION1_NAME);
		
		// When
		Division division = divisionRepository.save(newDivision);
		
		// Then
		assertNotNull(division);
		assertNotNull(division.getDivisionId());
		assertEquals (NEW_DIVISION1_NAME, division.getDivisionName());
	}
	
	@Test
	public void findAllShouldReturnAllPersistedObjects () {
		// Given
		divisionRepository.save(domainObjectFactory.createDivision(NEW_DIVISION1_NAME));
		divisionRepository.save(domainObjectFactory.createDivision(NEW_DIVISION2_NAME));
		
		// When
		Iterable<Division> divisions = divisionRepository.findAll();
		
		// Then
		assertTrue (divisions.spliterator().estimateSize() >= 2);
	}
	
	@Test
	public void createOrUpdateShouldPersistANewObject () {
		// Given
		Division newDivision = domainObjectFactory.createDivision(NEW_DIVISION1_NAME);
		
		// When
		Division division = divisionRepository.createOrUpdate(newDivision);
		
		// Then
		assertNotNull(division);
		assertNotNull(division.getDivisionId());
		assertEquals (NEW_DIVISION1_NAME, division.getDivisionName());
	}
	
	@Test
	public void findMatchingShouldReturnAMatchingPersistedObject () {
		// Given
		Division newDivision = domainObjectFactory.createDivision(NEW_DIVISION1_NAME);
		Division division = divisionRepository.save(newDivision);
		
		// When
		division = divisionRepository.findMatching(newDivision);
		
		// Then
		assertEquals (NEW_DIVISION1_NAME, division.getDivisionName());
		assertNotNull(division.getDivisionId());
	}
	
	@Test
	public void findOneShouldReturnAMatchingPersistedObject () {
		// Given
		Division newDivision = domainObjectFactory.createDivision(NEW_DIVISION1_NAME);
		Division division = divisionRepository.save(newDivision);
		String newDivisionId = division.getDivisionId();
		
		// When
		division = divisionRepository.findOne(newDivisionId);
		
		// Then
		assertNotNull(division);
		assertEquals (NEW_DIVISION1_NAME, division.getDivisionName());
		assertEquals (newDivisionId, division.getDivisionId());
	}
	
	@Test
	public void deleteShouldRemoveADivision () {
		// Given
		Division newDivision = domainObjectFactory.createDivision(NEW_DIVISION1_NAME);
		Division division = divisionRepository.save(newDivision);
		String newDivisionId = division.getDivisionId();
		
		// When
		divisionRepository.delete(division);
		
		// Then
		division = divisionRepository.findOne(newDivisionId);
		assertNull(division);
	}
}
