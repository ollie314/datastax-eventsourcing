package com.datastax.events.data;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.datastax.event.model.Event;

public class EventGenerator {

	private static DateTime date = new DateTime().minusDays(100).withTimeAtStartOfDay();
	private static List<String> eventTypes = Arrays.asList("INSERT", "DELETE", "LOGIN", "LOGOUT", "PREFERENCES", "CHANGE_OF_PASSWORD",
			"LOG", "ERROR", "UNSUBSCRIBE", "SUBSCRIBE");
	private static List<String> aggregateTypes = Arrays.asList("DAILY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY");
	
	public static Event createRandomEvent(int noOfEvents) {
		
		// create time by adding a random no of seconds to the midnight of yesterday.
		date = date.plusMillis(new Double(Math.random() * 200).intValue() + 1);
		
		Event event = new Event();
		event.setId(UUID.randomUUID());
		event.setEventtype(eventTypes.get(new Double(Math.random()*eventTypes.size()).intValue()));
		event.setData("Some data " + date.toString());
		event.setTime(date.toDate());
		event.setAggregateType(aggregateTypes.get(new Double(Math.random()*aggregateTypes.size()).intValue()));
		
		return event; 
	}	
}
