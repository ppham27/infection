package com.phillypham.ka.infection;

import java.util.TreeSet;
import java.util.Set;

public class User implements Comparable<User> {
	private String id;
	private boolean infected = false;
	private Set<User> students;
	private Set<User> coaches;
	
	public User(String id) {
		this.id = id;
		students = new TreeSet<User>();
		coaches = new TreeSet<User>();
	}
	
	public String getId() { return this.id; }
	public Set<User> getStudents() { return this.students; }
	public Set<User> getCoaches() { return this.coaches; }
	public boolean addStudent(User student) {
		student.coaches.add(this);
		return students.add(student);
	}
	
	public boolean addCoach(User coach) {
		coach.students.add(this);
		return coaches.add(coach);
	}
	
	public boolean isInfected() { return this.infected; }
	
	public boolean infect() {
		boolean changed = !infected;
		this.infected = true;
		return changed;
	}
	
	public boolean uninfect() {
		boolean changed = infected;
		this.infected = false;
		return changed;
	}
	
	public void toggleInfection() { infected = !infected; }
	
	public int compareTo(User other) {
		return this.getId().compareTo(other.getId());
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof User) {
			User otherUser = (User) other;
			return this.getId() == otherUser.getId();
		}		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("User: "); out.append(id);
		return out.toString();
	}	
}
