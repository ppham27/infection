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
		if (!args[1].equals("user") && !args[1].equals("limit")) {
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
		Map<String, Set<User>> componentsInfected = new TreeMap<String, Set<User>>();
		if (args[1].equals("user")) {
			if (users.userExists(args[2])) {
				componentsInfected.put(args[2], infector.infect(args[2]));
			} else {
				cout.println("Error: " + args[2] + " is not a valid user.");
				cout.close(); System.exit(-1);
			}
		} else {
			// args[2].equals("limit")
			int limit = -1; 
			try {
				limit = Integer.parseInt(args[2]);
			} catch(Exception e) {
				cout.println("Error: " + args[2] + " is not an integer.");
				cout.close(); System.exit(-1);				
			}
			// associate a component with a user in that component
			Set<User> visited = new TreeSet<User>();
			Map<String, Integer> componentSize = new TreeMap<String, Integer>();
			for (String id : userIds) {
				if (!visited.contains(users.getUser(id))) {
					Set<User> component = infector.getComponent(id);
					visited.addAll(component);
					componentSize.put(id, component.size());
				}
			}
			// now solve the knapsack problem, we have a knapsack of size limit
			// we want to fill our knapsack with components
			Set<String> selectedComponents = selectComponents(componentSize, limit);
			if (selectedComponents.size() == 0) {
				int minComponentSize = Integer.MAX_VALUE;
				for (int size : componentSize.values()) { 
					if (size < minComponentSize) { minComponentSize = size; }
				}
				cout.println("Warning: no component was infected. The smallest component is of size " + minComponentSize + ".");
				cout.close(); System.exit(0);
			}
			for (String id : selectedComponents) {
				// infect the selected components
				componentsInfected.put(id, infector.infect(id));
			}
		}
		
		// print each component on a separate line
		int totalUsersInfected = 0;
		for (Set<User> component : componentsInfected.values()) {			
			Iterator<User> it = component.iterator();
			cout.print(it.next().getId());
			while (it.hasNext()) {
				cout.print(' '); cout.print(it.next().getId());
			}
			cout.println();
			totalUsersInfected += component.size();
		}
		cout.println();
		cout.println(totalUsersInfected + " users were infected.");
		fin.close();
		cout.close();
	}
		
	/**
	 * Fill a knapsack of size limit with components.
	 * @param componentSize a map of components and their sizes
	 * @param limit the size of our knapsack
	 * @return a set of the components that we select
	 */
	public static Set<String> selectComponents(Map<String, Integer> componentSize, int limit) {
		class State {
			int score;
			Set<String> componentsUsed;
			public State() {
				this.score = 0; this.componentsUsed = new TreeSet<String>();
			}
			public State(int score, Set<String> componentsUsed) {
				this.score = score;
				this.componentsUsed = new TreeSet<String>();
				this.componentsUsed.addAll(componentsUsed);
			} 
		}		
		// solve knapsack problem with dynamic programming O(limit*numComponents)
		State[] bestState = new State[limit + 1];
		for (int i = 0; i <= limit; ++i) { bestState[i] = new State(); }
		for (Map.Entry<String, Integer> e : componentSize.entrySet()) {
			String id = e.getKey();
			int size = e.getValue();
			for (int s = limit; s >= size; --s) {
				if (bestState[s].score < bestState[s-size].score + size || 
						(bestState[s].score == bestState[s-size].score + size && 
						bestState[s].componentsUsed.size() > bestState[s-size].componentsUsed.size() + 1)) {
					// break ties by choosing the set with less components
					bestState[s] = new State(bestState[s-size].score + size, bestState[s-size].componentsUsed);
					bestState[s].componentsUsed.add(id);
				}
			}
		}
		return bestState[limit].componentsUsed;
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
	
	public Set<User> getComponent(String id) {
		return changeInfectionState(id, users.getUser(id).isInfected());
	}
	
	public int count(String id) {
		// presumably everyone in the component has the same infection state
		return changeInfectionState(id, users.getUser(id).isInfected()).size();
	}
}
