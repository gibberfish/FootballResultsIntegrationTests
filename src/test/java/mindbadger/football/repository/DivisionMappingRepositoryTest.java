package mindbadger.football.repository;

import static org.junit.Assert.*;

import org.assertj.core.api.AssertDelegateTarget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mindbadger.TestApplication;
import mindbadger.football.domain.DivisionMapping;
import mindbadger.football.domain.DomainObjectFactory;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class})
public class DivisionMappingRepositoryTest {
	private static final String DIALECT = "mydialect";
	private static final String INVALID_DIALECT = "INVALID_ID";
	private static final Integer SOURCE_ID1 = 10;
	private static final Integer FRA_ID1 = 20;
	private static final Integer SOURCE_ID2 = 11;
	private static final Integer FRA_ID2 = 21;

	@Autowired
	private DivisionMappingRepository divisionMappingRepository;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@After
	@Before
	public void ensureAnyRemainingTestDataIsClearedBeforeTestsRun() {
		DivisionMapping newDivisionMapping = domainObjectFactory.createDivisionMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		DivisionMapping divisionMapping = divisionMappingRepository.findOne(newDivisionMapping);
		if (divisionMapping != null) divisionMappingRepository.delete(divisionMapping);
		
		newDivisionMapping = domainObjectFactory.createDivisionMapping(DIALECT, SOURCE_ID2, FRA_ID2);
		divisionMapping = divisionMappingRepository.findOne(newDivisionMapping);
		if (divisionMapping != null) divisionMappingRepository.delete(divisionMapping);
	}
	
	@Test
	public void findOneShouldReturnNullIfANullIdIsPassedIn () {
		// When
		DivisionMapping divisionMapping = divisionMappingRepository.findOne(null);
		
		// Then
		assertNull(divisionMapping);
	}
	
	@Test
	public void findOneShouldReturnNullIfANonExistentObjectIsPassedIn () {
		// When
		DivisionMapping invalidDivsionMapping = domainObjectFactory.createDivisionMapping(INVALID_DIALECT, 0, 0);
		DivisionMapping divisionMapping = divisionMappingRepository.findOne(invalidDivsionMapping);
		
		// Then
		assertNull(divisionMapping);
	}
	
	@Test
	public void saveShouldPersistANewObject () {
		// Given
		DivisionMapping newDivisionMapping = domainObjectFactory.createDivisionMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		
		// When
		DivisionMapping divisionMapping = divisionMappingRepository.save(newDivisionMapping);
		
		// Then
		assertNotNull(divisionMapping);
		assertEquals(DIALECT, divisionMapping.getDialect());
		assertEquals (SOURCE_ID1, divisionMapping.getSourceId());
		assertEquals (FRA_ID1, divisionMapping.getFraId());
	}
	
	@Test
	public void findAllShouldReturnAllPersistedObjects () {
		// Given
		divisionMappingRepository.save(domainObjectFactory.createDivisionMapping(DIALECT, SOURCE_ID1, FRA_ID1));
		divisionMappingRepository.save(domainObjectFactory.createDivisionMapping(DIALECT, SOURCE_ID2, FRA_ID2));
		
		// When
		Iterable<DivisionMapping> divisionMappings = divisionMappingRepository.findAll();
		
		// Then
		assertTrue (divisionMappings.spliterator().estimateSize() >= 2);
	}
	
	@Test
	public void createOrUpdateShouldThrowAnException () {
		// Given
		DivisionMapping newDivisionMapping = domainObjectFactory.createDivisionMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		
		try {
			// When
			divisionMappingRepository.createOrUpdate(newDivisionMapping);
			fail ("Should have thrown an exception here");
		} catch (RuntimeException e) {
			assertEquals ("This method is not implemented for DivisionMappings", e.getMessage());
		}
	}
	
	@Test
	public void findMatchingShouldReturnAMatchingPersistedObject () {
		// Given
		DivisionMapping newDivisionMapping = domainObjectFactory.createDivisionMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		DivisionMapping divisionMapping = divisionMappingRepository.save(newDivisionMapping);
		
		try {
			// When
			divisionMappingRepository.findMatching(divisionMapping);
			fail ("Should have thrown an exception here");
		} catch (RuntimeException e) {
			assertEquals ("This method is not implemented for DivisionMappings", e.getMessage());
		}
	}
	
	@Test
	public void findOneShouldReturnAMatchingPersistedObject () {
		// Given
		DivisionMapping newDivisionMapping = domainObjectFactory.createDivisionMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		DivisionMapping divisionMapping = divisionMappingRepository.save(newDivisionMapping);
		
		// When
		divisionMapping = divisionMappingRepository.findOne(newDivisionMapping);
		
		// Then
		assertNotNull(divisionMapping);
		assertEquals(DIALECT, divisionMapping.getDialect());
		assertEquals (SOURCE_ID1, divisionMapping.getSourceId());
		assertEquals (FRA_ID1, divisionMapping.getFraId());
	}
	
	@Test
	public void deleteShouldRemoveADivisionMapping () {
		// Given
		DivisionMapping newDivisionMapping = domainObjectFactory.createDivisionMapping(DIALECT, SOURCE_ID1, FRA_ID1);
		DivisionMapping divisionMapping = divisionMappingRepository.save(newDivisionMapping);
		
		// When
		divisionMappingRepository.delete(divisionMapping);
		
		// Then
		divisionMapping = divisionMappingRepository.findOne(newDivisionMapping);
		assertNull(divisionMapping);
	}
}
