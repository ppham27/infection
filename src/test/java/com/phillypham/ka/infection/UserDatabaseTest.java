package com.phillypham.ka.infection;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UserDatabaseTest {
	UserDatabase emptyUsers;
	UserDatabase users;
	@Before
	public void setUp() throws Exception {
		emptyUsers = new UserDatabase();
		users = new UserDatabase();
		users.addUser("a"); users.addUser("b"); users.addUser("c"); 
		users.addRelationship("a", "b");
	}
	
	@Test
	public void testUserDatabase() {
		String[] userIds = new String[]{"0","1","2","3","4"};
		String[] studentIds = new String[]{"1", "2", "3"};
		String[] coachIds = new String[]{"2", "3", "4"};
		UserDatabase newUsers = new UserDatabase(userIds, studentIds, coachIds);
		for (String id : userIds) {
			assertTrue(newUsers.userExists(id));
		}
		// make sure relations are there
		for (int i = 0; i < studentIds.length; ++i) {
			String studentId = studentIds[i];
			String coachId = coachIds[i];
			assertTrue(newUsers.getUser(studentId).getCoaches().contains(newUsers.getUser(coachId)));
			assertTrue(newUsers.getUser(coachId).getStudents().contains(newUsers.getUser(studentId)));
		}		
	}

	@Test
	public void testAddRelationship() {
		assertFalse(users.getUser("a").getCoaches().contains(users.getUser("c")));
		users.addRelationship("a", "c");
		assertTrue(users.getUser("a").getCoaches().contains(users.getUser("c")));
	}

	@Test
	public void testAddUser() {
		emptyUsers.addUser("1");
		assertTrue(emptyUsers.userExists("1"));		
	}

	@Test
	public void testGetUser() {
		User u = users.getUser("a");
		assertEquals("a", u.getId()); 
	}

	@Test
	public void testUserExists() {
		assertTrue(users.userExists("a"));
		assertFalse(users.userExists("d"));
	}

}
