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
import mindbadger.football.domain.TrackedDivision;
import mindbadger.football.domain.DomainObjectFactory;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class})
public class TrackedDivisionRepositoryTest {
	private static final String DIALECT = "mydialect";
	private static final String INVALID_DIALECT = "INVALID_ID";
	private static final Integer SOURCE_ID1 = 10;
	private static final Integer SOURCE_ID2 = 11;

	@Autowired
	private TrackedDivisionRepository trackedDivisionRepository;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@After
	@Before
	public void ensureAnyRemainingTestDataIsClearedBeforeTestsRun() {
		TrackedDivision newTrackedDivision = domainObjectFactory.createTrackedDivision(DIALECT, SOURCE_ID1);
		TrackedDivision trackedDivision = trackedDivisionRepository.findOne(newTrackedDivision);
		if (trackedDivision != null) trackedDivisionRepository.delete(trackedDivision);
		
		newTrackedDivision = domainObjectFactory.createTrackedDivision(DIALECT, SOURCE_ID2);
		trackedDivision = trackedDivisionRepository.findOne(newTrackedDivision);
		if (trackedDivision != null) trackedDivisionRepository.delete(trackedDivision);
	}
	
	@Test
	public void findOneShouldReturnNullIfANullIdIsPassedIn () {
		// When
		TrackedDivision trackedDivision = trackedDivisionRepository.findOne(null);
		
		// Then
		assertNull(trackedDivision);
	}
	
	@Test
	public void findOneShouldReturnNullIfANonExistentObjectIsPassedIn () {
		// When
		TrackedDivision invalidDivsionMapping = domainObjectFactory.createTrackedDivision(INVALID_DIALECT, 0);
		TrackedDivision trackedDivision = trackedDivisionRepository.findOne(invalidDivsionMapping);
		
		// Then
		assertNull(trackedDivision);
	}
	
	@Test
	public void saveShouldPersistANewObject () {
		// Given
		TrackedDivision newTrackedDivision = domainObjectFactory.createTrackedDivision(DIALECT, SOURCE_ID1);
		
		// When
		TrackedDivision trackedDivision = trackedDivisionRepository.save(newTrackedDivision);
		
		// Then
		assertNotNull(trackedDivision);
		assertEquals(DIALECT, trackedDivision.getDialect());
		assertEquals (SOURCE_ID1, trackedDivision.getSourceId());
	}
	
	@Test
	public void findAllShouldReturnAllPersistedObjects () {
		// Given
		trackedDivisionRepository.save(domainObjectFactory.createTrackedDivision(DIALECT, SOURCE_ID1));
		trackedDivisionRepository.save(domainObjectFactory.createTrackedDivision(DIALECT, SOURCE_ID2));
		
		// When
		Iterable<TrackedDivision> trackedDivisions = trackedDivisionRepository.findAll();
		
		// Then
		assertTrue (trackedDivisions.spliterator().estimateSize() >= 2);
	}
	
	@Test
	public void createOrUpdateShouldThrowAnException () {
		// Given
		TrackedDivision newTrackedDivision = domainObjectFactory.createTrackedDivision(DIALECT, SOURCE_ID1);
		
		try {
			// When
			trackedDivisionRepository.createOrUpdate(newTrackedDivision);
			fail ("Should have thrown an exception here");
		} catch (RuntimeException e) {
			assertEquals ("This method is not implemented for TrackedDivisions", e.getMessage());
		}
	}
	
	@Test
	public void findMatchingShouldReturnAMatchingPersistedObject () {
		// Given
		TrackedDivision newTrackedDivision = domainObjectFactory.createTrackedDivision(DIALECT, SOURCE_ID1);
		TrackedDivision trackedDivision = trackedDivisionRepository.save(newTrackedDivision);
		
		try {
			// When
			trackedDivisionRepository.findMatching(trackedDivision);
			fail ("Should have thrown an exception here");
		} catch (RuntimeException e) {
			assertEquals ("This method is not implemented for TrackedDivisions", e.getMessage());
		}
	}
	
	@Test
	public void findOneShouldReturnAMatchingPersistedObject () {
		// Given
		TrackedDivision newTrackedDivision = domainObjectFactory.createTrackedDivision(DIALECT, SOURCE_ID1);
		TrackedDivision trackedDivision = trackedDivisionRepository.save(newTrackedDivision);
		
		// When
		trackedDivision = trackedDivisionRepository.findOne(newTrackedDivision);
		
		// Then
		assertNotNull(trackedDivision);
		assertEquals(DIALECT, trackedDivision.getDialect());
		assertEquals (SOURCE_ID1, trackedDivision.getSourceId());
	}
	
	@Test
	public void deleteShouldRemoveATrackedDivision () {
		// Given
		TrackedDivision newTrackedDivision = domainObjectFactory.createTrackedDivision(DIALECT, SOURCE_ID1);
		TrackedDivision trackedDivision = trackedDivisionRepository.save(newTrackedDivision);
		
		// When
		trackedDivisionRepository.delete(trackedDivision);
		
		// Then
		trackedDivision = trackedDivisionRepository.findOne(newTrackedDivision);
		assertNull(trackedDivision);
	}
}
