package com.datastax.events;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.KillableRunner;
import com.datastax.demo.utils.PropertyHelper;
import com.datastax.demo.utils.Timer;
import com.datastax.event.model.Event;
import com.datastax.events.service.EventService;

public class ReadEvents {

	private static Logger logger = LoggerFactory.getLogger(ReadEvents.class);

	public ReadEvents() {

		BlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(100);
		
		//Executor for Threads
		int noOfThreads = Integer.parseInt(PropertyHelper.getProperty("noOfThreads", "8"));
		
		ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
		EventService service = new EventService();
		EventReader eventReader = new EventReader(queue);
		executor.execute(eventReader);
		
		DateTime from = DateTime.now().withDate(2015, 11, 4).withTime(12, 0, 0, 0);
		DateTime to = DateTime.now().withDate(2015, 11, 6).withTime(12, 0, 0, 0);
		
		Timer timer = new Timer();
		service.getEvents(queue, from, to, null);
		
		timer.end();			
		
		long count = eventReader.getCount();
		
		logger.info("Time taken to read " + count + " events was " + timer.getTimeTakenSeconds() + " secs");
		
		timer = new Timer();
		
		//Only get preferences
		service.getEvents(queue, from, to, "PREFERENCES");
		
		timer.end();			
		long count1 = eventReader.getCount() - count;
		logger.info("Time taken to read " + count1 + " events was " + timer.getTimeTakenSeconds() + " secs");
		
		System.exit(0);	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ReadEvents();

		System.exit(0);
	}

}
