package com.phillypham.ka.infection;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class UserTest {

	@Test
	public void testUser() {		
		User u0 = new User("a");
		assertEquals("a", u0.getId());
		assertFalse(u0.isInfected());		
	}
	
	@Test
	public void testCompareTo() {
		User[] users = new User[3];		
		users[0] = new User("a");
		users[1] = new User("c");
		users[2] = new User("b");
		Arrays.sort(users);
		assertEquals(new User("a"), users[0]);
		assertEquals(new User("b"), users[1]);
		assertEquals(new User("c"), users[2]);
	}

	@Test
	public void testGetId() {
		User u = new User("abc");
		assertEquals(u.getId(), "abc");
	}

	@Test
	public void testAddStudent() {
		User student = new User("student");
		User coach = new User("coach");
		coach.addStudent(student);
		assertTrue(student.getCoaches().contains(coach));
		assertTrue(coach.getStudents().contains(student));
	}

	@Test
	public void testAddCoach() {
		User student = new User("student");
		User coach = new User("coach");
		student.addCoach(coach);
		assertTrue(student.getCoaches().contains(coach));
		assertTrue(coach.getStudents().contains(student));
	}

	@Test
	public void testInfect() {
		User u = new User("abc");
		u.infect();
		assertTrue(u.isInfected());		
	}

	@Test
	public void testUninfect() {
		User u = new User("abc");
		u.infect();
		u.uninfect();
		assertFalse(u.isInfected());
	}

	@Test
	public void testToggleInfection() {
		User u = new User("abc");
		u.toggleInfection();
		assertTrue(u.isInfected());
		u.toggleInfection();
		assertFalse(u.isInfected());
	}

}
