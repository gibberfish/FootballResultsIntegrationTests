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
import mindbadger.football.domain.Division;
import mindbadger.football.domain.DomainObjectFactory;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class})
public class DivisionRepositoryTest {
	private static final String NEW_DIVISION_NAME = "New Division";
	private static final String INVALID_ID = "INVALID_ID";

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@Before
	public void init() {
		Division division = domainObjectFactory.createDivision(NEW_DIVISION_NAME);
		division = divisionRepository.findMatching(division);
		if (division != null ) divisionRepository.delete(division);
	}
	
	@Test
	public void testDivisionRepository() {
		// Attempt to find a division with a null ID
		Division division = divisionRepository.findOne(null);
		assertNull(division);

		// Attempt to find a division with an ID that doesn't exist
		division = divisionRepository.findOne(INVALID_ID);
		assertNull(division);
		
		// Attempt to find a division matching a non-persisted domain object
		Division newDivision = domainObjectFactory.createDivision(NEW_DIVISION_NAME);
		division = divisionRepository.findMatching(newDivision);
		assertNull(division);

		// Save the new domain object
		division = divisionRepository.save(newDivision);
		assertNotNull(division);
		assertNotNull(division.getDivisionId());
		assertEquals (NEW_DIVISION_NAME, division.getDivisionName());

		// Attempt to find a division matching the now-persisted domain object
		division = divisionRepository.findMatching(newDivision);
		assertEquals (NEW_DIVISION_NAME, division.getDivisionName());
		assertNotNull(division.getDivisionId());
		
		// Attempt to find a division using the ID of the newly persisted domain object
		String newDivisionId = division.getDivisionId();
		division = divisionRepository.findOne(newDivisionId);
		assertNotNull(division);
		assertEquals (NEW_DIVISION_NAME, division.getDivisionName());
		assertEquals (newDivisionId, division.getDivisionId());
		
		// Delete the domain object and check it can no longer be retrieved
		divisionRepository.delete(division);
		division = divisionRepository.findOne(newDivisionId);
		assertNull(division);
	}
}
