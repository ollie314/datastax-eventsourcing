package com.datastax.events;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.KillableRunner;
import com.datastax.event.model.Event;

class EventReader implements KillableRunner {

	private Logger logger = LoggerFactory.getLogger(EventReader.class);

	private volatile boolean shutdown = false;
	private BlockingQueue<Event> queue;
	private AtomicLong counter = new AtomicLong(0);

	public EventReader(BlockingQueue<Event> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		Event event;
		while (!shutdown) {
			event = queue.poll();
			
			if (event!=null){
				//logger.info(event.getTime() + "-" + event.getEventtype() + "-" + event.getData());
				counter.incrementAndGet();
			}
		}
	}

	@Override
	public void shutdown() {
		shutdown = true;
	}

	public long getCount() {
		return counter.get();
		
	}
}