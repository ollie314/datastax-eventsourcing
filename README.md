Event Sourcing
========================


To create the schema, run the following

	mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaSetup" -DcontactPoints=localhost
	
To create some transactions, run the following 
	
	mvn clean compile exec:java -Dexec.mainClass="com.datastax.events.Main"  -DcontactPoints=localhost

To remove the tables and the schema, run the following.

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaTeardown"
    
    
