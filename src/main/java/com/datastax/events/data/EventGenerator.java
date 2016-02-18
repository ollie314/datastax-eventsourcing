package com.datastax.events.data;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.datastax.events.model.Event;


public class EventGenerator	{

	private static final long DAY_MILLIS = 1000 * 60 *60 * 24;
	private static List<String> eventTypes = Arrays.asList("INSERT", "DELETE", "LOGIN", "LOGOUT", "LOGIN", "LOGOUT", 
			"LOGIN", "LOGOUT", "LOGIN", "LOGOUT", "LOGIN", "LOGOUT", "LOGIN", "LOGOUT", "LOGIN", "LOGOUT", "LOGIN", "LOGOUT", 
			"LOGIN", "LOGOUT", "LOGIN", "LOGOUT", "LOGIN", "LOGOUT", "LOGIN", "LOGOUT", "LOGIN", "LOGOUT", "LOGIN", "LOGOUT", 
			"PREFERENCES", "CHANGE_OF_PASSWORD",
			"LOG", "ERROR", "ERROR", "ERROR", "ERROR", "UNSUBSCRIBE", "SUBSCRIBE", "SUBSCRIBE", "SUBSCRIBE", "SUBSCRIBE");
	private static List<String> aggregateTypes = Arrays.asList("DAILY", "DAILY", "DAILY", "DAILY", "DAILY", "WEEKLY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY");
	private static List<String> logTypes = Arrays.asList("INFO","INFO","INFO","INFO", "LOG", "INFO", "LOG", "INFO", "LOG", "INFO", "LOG", "INFO","DEBUG", "ERROR", "WARN");
	private static String host = "100.1.2.";
	
	public static Event createRandomEvent(int noOfEvents, int noOfDays) {
		
		long noOfMillis = noOfDays * DAY_MILLIS;
		 		
		// create time by adding a random no of millis			
		long millis = DateTime.now().getMillis() - (new Double(Math.random() * noOfMillis).longValue() + 1l);			
		DateTime newDate = DateTime.now().withMillis(millis);

		Event event = EventGenerator.createEvent();
		event.setTime(newDate.toDate());
		return event; 
	}	
	
	public static Event createRandomEventNow() {
		
		Event event = EventGenerator.createEvent();		
		DateTime newDate = DateTime.now();
		event.setTime(newDate.toDate());		
		return event; 
	}

	private static Event createEvent() {
		Event event = new Event();
		event.setId(UUID.randomUUID());
		event.setEventtype(eventTypes.get(new Double(Math.random()*eventTypes.size()).intValue()));
		event.setData("Some data " + DateTime.now().toString() + " " + Math.random());
		event.setHost(host + new Double(Math.random()*100).intValue());
		event.setLoglevel(logTypes.get(new Double(Math.random()*logTypes.size()).intValue()));
		event.setAggregateType(aggregateTypes.get(new Double(Math.random()*aggregateTypes.size()).intValue()));
		
		return event; 
	}	
}
