Event Sourcing
========================

This demo shows how Cassandra and DSE can be using to store and replay events. 

To use Spark you will need to provide your own Cassandra and Spark deployments. In this demo we will use DSE as they are already integrated.

First we start DSE in spark mode - 
http://docs.datastax.com/en/datastax_enterprise/4.8/datastax_enterprise/startStop/refDseStartStopDse.html

The implementation uses bucketing to group all data into particular time buckets for replay. The time bucket used in this example is 1 minute but any time bucket can be used.   

To create the schema, run the following

	mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaSetup" -DcontactPoints=localhost
	
To create events, run the following (Default of 10 million events) 
	
	mvn clean compile exec:java -Dexec.mainClass="com.datastax.events.Main"  -DcontactPoints=localhost -DnoOfEvents=10000000
	
To replay a sample event set, run 

	mvn clean compile exec:java -Dexec.mainClass="com.datastax.events.ReadEvents"  -DcontactPoints=localhost
	
This replays 2 scenarios

	1. Replay all events for a specified time range
	2. Replay all events for a specified time range and a specific event type.		
			
To run the webservice

	mvn jetty:run
	
To run a rest query, go the brower and enter a url in the format http://localhost:8080/datastax-eventsourcing/rest/getevents/from/to, 
where the date format is 'yyyyMMdd-hhmmss' e.g. For all events from midnight to 1:00 on the 1st of November 2015 run - 

	http://localhost:8080/datastax-eventsourcing/rest/getevents/20151101-000000/20151101-010000/

To remove the tables and the schema, run the following.

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaTeardown"
    
    

    