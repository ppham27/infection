infection
====

This is my attempt at the Infection project. There are two modes of running. 

In one mode, you can selected a user to infect the component in which that user resides. Users are infected with a breadth-first search (BFS). 

In the other mode, you can limit the number of infections. I interpreted the *limit* as a hard upper limit, so the number of infections has to be less than or equal to the user-given *limit*. Thus, it reduces to a knapsack problem. We have a knapsack of size *limit*, and are items are connected components with value and weight equal to the number of users in the component. Thus, it can be solved with dynamic programming in O((*Number of users*) + (*Number of components*)(*limit*)) time.

I opted to create a visualization. If I were to limit infections to exactly a certain number of users, I would first run the knapsack the procedure to get a number that is lower than the limit. Next, I would create a heuristic based around the depth of infection spread and connectedness of the subgraph. If we can't infect a whole component, it's better to infect a dense area of the graph. Possibly, we could try to identify bridges or use a min-cut to create smaller components. 

Another improvement would be to use a better method to solve the knapsack problem. Dynamic programming would not scale if *limit* or *Number of components* is large. It's probably not important to find the exact optimal solution, so heuristic searches that find semi-optimal solutions would be suitable here. The knapsack framework is flexible and lets us select components using other factors than just their size. It's possible that we want to assign larger components higher value for instance.

## Usage
Build with Maven, using 

```
mvn install
```

in the infection directory.

The user database is built from a file such as sample_user_data/users00.txt.

```text
10 5
0
1
2
3
4
5
6
7
8
9
1 5
8 9
7 8
3 1
5 4
```

The first line is a pair of integers, N and M. N is the number of users, and M is the number of student-coach pairs. The following N lines are user ids (lines 2 to N + 1). The following M lines are student-coach pairs, where the student id precedes the coach id (lines N + 2 to N + M + 1). The user ids cannot have spaces.

To run,

```
java -cp target/infection-1.0-SNAPSHOT.jar com.phillypham.ka.infection.Infector <user data file> (user|limit) (user id|limit)
```

If the second argument is user, then the third argument is user id. If the second argument is limit, then the third argument must be an integer. Output is the user ids to be infected with each connected component on a separate line.

For example,

```
java -cp target/infection-1.0-SNAPSHOT.jar com.phillypham.ka.infection.Infector sample_user_data/users00.txt user 1
```

infects all users in the same connected component as user 1.

```
java -cp target/infection-1.0-SNAPSHOT.jar com.phillypham.ka.infection.Infector sample_user_data/users01.txt limit 14 > output.txt
```

infects less than or equal to 14 users.

## Visualization

To visualize, use the tools in the visualization folder. In the visualization folder, run

```
python -m http.server 8888
```

and visit `http://localhost:8888/`.

One can use the included graph.json file. Preparing the necessary graph.json file requires several steps.

1. First, prepare your user data file similar to say sample_user_data/users01.txt.
2. Then, get output from the java program. For example, `java -cp target/infection-1.0-SNAPSHOT.jar com.phillypham.ka.infection.Infector sample_user_data/users01.txt limit 21 > output.txt`.
3. Finally, transform data with `python visualization/transform_graph_to_json.py`. For intance, `python visualization/transform_graph_to_json.py sample_user_data/users01.txt output.txt` will produce a graph.json file in the present working directory.
4. Finally, copy graph.json to the visualization folder, run `python -m http.server 8888` and visit `http://localhost:8888/`.