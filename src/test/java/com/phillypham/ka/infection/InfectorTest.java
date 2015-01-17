package com.phillypham.ka.infection;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

public class InfectorTest {
	Infector infectorA;
	UserDatabase usersA;
	static String[] userIdsA;
	static String[] studentIdsA;
	static String[] coachIdsA;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		userIdsA = new String[]{"0","1","2","3","4","5","6","7","8","9","10"};
		studentIdsA = new String[]{"1","8","7","3","5"};
		coachIdsA = new String[]{"5","9","8","1","4"};
	}
	@Before
	public void setUp() throws Exception {				
		usersA = new UserDatabase(userIdsA, studentIdsA, coachIdsA);
		infectorA = new Infector(usersA);
	}

	@Test
	public void testChangeInfectionStateSingleInfection() {
		Set<User> actualInfectedUser = infectorA.infect("2");
		Set<User> expectedInfectedUser = new TreeSet<User>();
		expectedInfectedUser.add(usersA.getUser("2"));
		assertTrue(usersA.getUser("2").isInfected());
		assertEquals(expectedInfectedUser, actualInfectedUser);
		// uninfect
		actualInfectedUser = infectorA.uninfect("2");
		assertFalse(usersA.getUser("2").isInfected());
		assertEquals(expectedInfectedUser, actualInfectedUser);
	}
	
	@Test
	public void testChangeInfectionStateComponent() {
		Set<User> actualInfectedUser = infectorA.infect("1");
		Set<User> expectedInfectedUser = new TreeSet<User>();
		String[] expectedInfectedUserIds = new String[]{"1","3","4","5"};
		for (String id : expectedInfectedUserIds) {
			assertTrue(usersA.getUser(id).isInfected());
			expectedInfectedUser.add(usersA.getUser(id));
		}		
		assertEquals(expectedInfectedUser, actualInfectedUser);
		// uninfect
		actualInfectedUser = infectorA.uninfect("4");
		for (String id : expectedInfectedUserIds) {
			assertFalse(usersA.getUser(id).isInfected());		
		}		
		assertEquals(expectedInfectedUser, actualInfectedUser);
	}
	

	@Test
	public void testCount() {
		// count and don't change state
		assertEquals(1, infectorA.count("2"));
		assertFalse(usersA.getUser("2").isInfected());
		infectorA.infect("2");
		assertEquals(1, infectorA.count("2"));
		assertTrue(usersA.getUser("2").isInfected());
		// count if more than 1
		assertEquals(4, infectorA.count("3"));
		assertFalse(usersA.getUser("3").isInfected());
		infectorA.infect("5");
		assertEquals(4, infectorA.count("1"));
		assertTrue(usersA.getUser("4").isInfected());
	}

}
