package com.datastax.events;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.KillableRunner;
import com.datastax.demo.utils.PropertyHelper;
import com.datastax.demo.utils.ThreadUtils;
import com.datastax.demo.utils.Timer;
import com.datastax.event.model.Event;
import com.datastax.events.data.EventGenerator;
import com.datastax.events.service.EventService;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public Main() {

		String noOfEventsStr = PropertyHelper.getProperty("noOfEvents", "10000000");

		BlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(100);
		List<KillableRunner> tasks = new ArrayList<>();
		
		//Executor for Threads
		int noOfThreads = Integer.parseInt(PropertyHelper.getProperty("noOfThreads", "2"));
		ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
		EventService service = new EventService();
		
		int noOfEvents = Integer.parseInt(noOfEventsStr);
		logger.info("Writing " + noOfEventsStr + " events");

		for (int i = 0; i < noOfThreads; i++) {
			
			KillableRunner task = new EventWriter(service, queue);
			executor.execute(task);
			tasks.add(task);
		}					
		
		Timer timer = new Timer();
		for (int i = 0; i < noOfEvents; i++) {
			
			try{
				queue.put(EventGenerator.createRandomEvent(noOfEvents));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		timer.end();		
		ThreadUtils.shutdown(tasks, executor);
		
		
		System.exit(0);
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();

		System.exit(0);
	}

}
