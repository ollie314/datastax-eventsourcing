package com.datastax.events.data;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.datastax.event.model.Event;

public class EventGenerator {

	//We can change this from the Main
	public static DateTime date = new DateTime().withDate(2015, 10, 31).withTimeAtStartOfDay();
	
	private static final int DAY_MILLIS = 1000 * 60 *60 * 24;
	private static List<String> eventTypes = Arrays.asList("INSERT", "DELETE", "LOGIN", "LOGOUT", "PREFERENCES", "CHANGE_OF_PASSWORD",
			"LOG", "ERROR", "UNSUBSCRIBE", "SUBSCRIBE");
	private static List<String> aggregateTypes = Arrays.asList("DAILY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY");
	
	public static Event createRandomEvent(int noOfEvents, int noOfDays) {
		
		int noOfMillis = noOfDays * DAY_MILLIS;
		// create time by adding a random no of millis 
		DateTime newDate = date.plusMillis(new Double(Math.random() * noOfMillis).intValue() + 1);

		Event event = new Event();
		event.setId(UUID.randomUUID());
		event.setEventtype(eventTypes.get(new Double(Math.random()*eventTypes.size()).intValue()));
		event.setData("Some data " + date.toString());
		event.setTime(newDate.toDate());
		event.setAggregateType(aggregateTypes.get(new Double(Math.random()*aggregateTypes.size()).intValue()));
		
		return event; 
	}	
}
