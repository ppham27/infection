infection
====

This is my attempt at the Infection project.

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

The first line is a pair of integers, N and M. N is the number of users, and M is the number of student-coach pairs. The following N lines are user ids. The following M lines are student-coach pairs, where the student id precedes the coach id.

To run,

```
java -cp ./target/infection-1.0-SNAPSHOT.jar com.phillypham.ka.infection.Infector <user data file> (user|limit) (user id|limit)
```

If the second argument is user, then the third argument is user id. If the second argument is limit, then the third argument must be an integer. Output is the user ids to be infected with each connected component on a separate line.

For example,

```
java -cp ./target/infection-1.0-SNAPSHOT.jar com.phillypham.ka.infection.Infector sample_user_data/users00.txt user 1
```

infects all users in the same connected component as user 1.



