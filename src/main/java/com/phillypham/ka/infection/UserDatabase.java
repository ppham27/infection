package com.phillypham.ka.infection;

import java.util.Map;
import java.util.TreeMap;
/**
 * This class exists so I can identify users with strings instead of creating a dummy user object
 * @author phil
 */
public class UserDatabase {
	
	private Map<String, User> users;
	
	public UserDatabase() {
		this.users = new TreeMap<String, User>();
	}
	
	public UserDatabase(String[] userIds, String[] studentIds, String[] coachIds) {
		for (String id : userIds) { this.addUser(id); };
		assert(studentIds.length == coachIds.length);
		for (int i = 0; i < studentIds.length; ++i) {
			addRelationship(studentIds[i], coachIds[i]);			
			
		}
	}
	
	public void addRelationship(User student, User coach) {
		addRelationship(student.getId(), coach.getId());
	}
	
	public void addRelationship(String student, String coach) {
		assert(users.containsKey(student));
		assert(users.containsKey(coach));
		users.get(student).addCoach(users.get(coach));
	}
	
	public boolean addUser(User u) {
		return addUser(u.getId());
	}
	
	public boolean addUser(String id) {
		if (userExists(id)) { return false; }
		users.put(id, new User(id));
		return true;
	}
	
	public User getUser(User u) {
		return getUser(u.getId());
	}
	
	public User getUser(String id) {
		return users.get(id);
	}
	
	public boolean userExists(User u) {
		return userExists(u.getId());
	}
	
	public boolean userExists(String id) {
		return users.containsKey(id);
	}
}
