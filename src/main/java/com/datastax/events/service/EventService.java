package com.datastax.events.service;

import java.util.List;

import org.apache.zookeeper.Transaction;
import org.joda.time.DateTime;

import com.datastax.demo.utils.PropertyHelper;
import com.datastax.event.model.Event;
import com.datastax.events.dao.EventDao;

public class EventService {
	
	private EventDao dao;

	public EventService(){
		String contactPointsStr = PropertyHelper.getProperty("contactPoints", "localhost");
		this.dao = new EventDao(contactPointsStr.split(","));				
	}
	
	public List<Transaction> getEvents(DateTime from, DateTime to) {

		return null;
	}

	public void insertEvent(Event event) {
		dao.insertEvent(event);
	}

}
