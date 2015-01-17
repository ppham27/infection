package com.phillypham.ka.infection;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
	
	@Test
	public void testSelectComponents() {
		Map<String, Integer> componentSize = new TreeMap<String, Integer>();
		componentSize.put("a", 16);		
		componentSize.put("c", 2);
		componentSize.put("h", 3);
		componentSize.put("m", 2);
		componentSize.put("t", 2);
		componentSize.put("w", 1);
		// case 1, simple sanity check
		Set<String> actualSelectedComponents = Infector.selectComponents(componentSize, 1);
		Set<String> expectedSelectedComponents = new TreeSet<String>();
		expectedSelectedComponents.add("w");
		assertEquals(expectedSelectedComponents, actualSelectedComponents);
		// case 2, break tie by choosing h
		actualSelectedComponents = Infector.selectComponents(componentSize, 3);
		expectedSelectedComponents.clear();
		expectedSelectedComponents.add("h");
		assertEquals(expectedSelectedComponents, actualSelectedComponents);
		// case 3, selected elements only have size 10
		actualSelectedComponents = Infector.selectComponents(componentSize, 12);
		expectedSelectedComponents.clear();
		expectedSelectedComponents.add("c"); expectedSelectedComponents.add("h");
		expectedSelectedComponents.add("m"); expectedSelectedComponents.add("t");
		expectedSelectedComponents.add("w");
		assertEquals(expectedSelectedComponents, actualSelectedComponents);
		
		// case4, greedy solution is not optimal, choose smaller elements
		componentSize.clear();
		componentSize.put("phil", 23);
		componentSize.put("chris", 5);
		componentSize.put("masato", 5);
		componentSize.put("tim", 5);
		componentSize.put("joon", 5);
		componentSize.put("dan", 5);
		actualSelectedComponents = Infector.selectComponents(componentSize, 25);
		expectedSelectedComponents.clear();
		expectedSelectedComponents.add("chris"); expectedSelectedComponents.add("masato");
		expectedSelectedComponents.add("tim"); expectedSelectedComponents.add("joon");
		expectedSelectedComponents.add("dan");
		assertEquals(expectedSelectedComponents, actualSelectedComponents);								
	}
}
