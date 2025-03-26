A multi-threaded client-server console app built in Java using JDBC, Socket programming, and the Command Pattern.  
Each client can log in, create posts, interact with moods, and perform admin/user actions — all via the console.

The first time we start the app we need to initialize the database by running the DbConnection class, positioned in the config folder.
Then each time we run it we only need to start the server class and the 1+ client classes.
