package com.phillypham.ka.infection;

import java.io.*;
import java.util.*;

public class Infector {
	
	private UserDatabase users;
	
	public Infector(UserDatabase users) { this.users = users; }
	
	public static void main(String[] args) throws IOException {
		PrintWriter cout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));		
		if (args.length != 3) {
			cout.println("Usage:");
			cout.println("Infector <user data file> (user|limit) (user id|limit)");
			cout.println("If the second argument is user, then the third argument is user id.");
			cout.println("If the second argument is limit, then the third argument must be an integer.");
			cout.println("Output is the user ids to be infected with each connected component on a separate line.");
			cout.close(); System.exit(-1);			
		}
		if (!args[2].equals("user") && !args[2].equals("limit")) {
			cout.println("The second arugment must be 'user' or 'limit'");
			cout.close(); System.exit(-1);
		}
		BufferedReader fin = new BufferedReader(new FileReader(args[0]));
		StringTokenizer st = new StringTokenizer(fin.readLine());
		int N = Integer.parseInt(st.nextToken()); // number of users
		int M = Integer.parseInt(st.nextToken()); // number of edges
		String[] userIds = new String[N]; 
		for (int i = 0; i < N; ++i) { userIds[i] = fin.readLine(); }
		String[] studentIds = new String[M]; String[] coachIds = new String[M];
		for (int i = 0; i < M; ++i) { 
			st = new StringTokenizer(fin.readLine());
			studentIds[i] = st.nextToken(); coachIds[i] = st.nextToken();
		}
		UserDatabase users = new UserDatabase(userIds, studentIds, coachIds);
		Infector infector = new Infector(users);		
		cout.println(users);
		fin.close();
		cout.close();
	}
	/**
	 * Given a user, this function visits everyone in the component and infects/uninfects them
	 * @param id the id of user to start the infection from
	 * @param infectionState whether to infect or uninfect users
	 * @return a set of users in the component that were visited
	 */
	public Set<User> changeInfectionState(String id, boolean infect) {
		class QueueState {
			public User user; public int depth;
			public QueueState(User user, int depth) { this.user = user; this.depth = depth; }
		}		
		// infect by bfs
		ArrayDeque<QueueState> q = new ArrayDeque<QueueState>();
		Set<User> addedToQueue = new TreeSet<User>();
		q.add(new QueueState(users.getUser(id), 0));
		addedToQueue.add(users.getUser(id));
		while (!q.isEmpty()) {
			QueueState state = q.remove();
			User currentUser = state.user;
			int currentDepth = state.depth;
			if (infect) { 
				currentUser.infect();
			} else {
				currentUser.uninfect();
			}			
			// queue up the coaches and students
			for (User coach : currentUser.getCoaches()) {
				if (!addedToQueue.contains(coach)) {
					addedToQueue.add(coach);
					q.add(new QueueState(coach, currentDepth + 1));
				}
			}
			for (User student : currentUser.getStudents()) {
				if (!addedToQueue.contains(student)) {
					addedToQueue.add(student);
					q.add(new QueueState(student, currentDepth + 1));
				}
			}
		}
		return addedToQueue;
	}
	
	public Set<User> infect(String id) {
		return changeInfectionState(id, true);
	}
	
	public Set<User> uninfect(String id) {
		return changeInfectionState(id, false);
	}
	
	public int count(String id) {
		// presumably everyone in the component has the same infection state
		return changeInfectionState(id, users.getUser(id).isInfected()).size();
	}
}
