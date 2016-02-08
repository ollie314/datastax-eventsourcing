package com.datastax.events;

import java.util.concurrent.BlockingQueue;

import org.apache.zookeeper.Transaction;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.KillableRunner;
import com.datastax.event.model.Event;
import com.datastax.events.webservice.EventWS;

class TransactionReader implements KillableRunner {

	private Logger logger = LoggerFactory.getLogger(TransactionReader.class);
	
	private volatile boolean shutdown = false;
		private BlockingQueue<Event> queue;

	public TransactionReader(BlockingQueue<Event> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		Event event;
		while(!shutdown){				
			event = queue.poll(); 
			logger.info(event.getTime() + "-" + event.getEventtype() + "-" + event.getData());
		}				
	}
	
	@Override
    public void shutdown() {
        shutdown = true;
    }
}