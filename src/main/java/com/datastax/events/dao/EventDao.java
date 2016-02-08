package com.datastax.events.dao;

import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.event.model.Event;

/**
 * Inserts into 2 tables
 * 
 * @author patrickcallaghan
 *
 */
public class EventDao {

	private static Logger logger = LoggerFactory.getLogger(EventDao.class);
	private Session session;

	private static String keyspaceName = "datastax";
	private static String eventTable = keyspaceName + ".eventsource";

	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

	private static final String INSERT_INTO_EVENTS = "insert into " + eventTable + "(date, minute, id, aggregatetype, data, time, eventtype) values (?,?,?,?,?,?,?)";
	private static final String SELECT_BY_DATE = "select date, minute, id, aggregatetype, data, time, eventtype from " + eventTable + " where date =? and minute = ?"; 
	
	private PreparedStatement insertEvent;
	private PreparedStatement selectByDate;
	private AtomicLong counter = new AtomicLong(0);

	public EventDao(String[] contactPoints) {

		Cluster cluster = Cluster.builder().addContactPoints(contactPoints).build();

		
		this.session = cluster.connect();

		this.insertEvent = session.prepare(INSERT_INTO_EVENTS);
		this.selectByDate = session.prepare(SELECT_BY_DATE);
	}

	public void insertEvent(Event event) {
		
		DateTime dateTime = new DateTime(event.getTime());
		dateTime.getMinuteOfDay();
		
		int minute = dateTime.getMinuteOfDay();
		String date = dateFormatter.format(dateTime.toDate());
		
		BoundStatement bs = new BoundStatement(this.insertEvent);
		bs.bind(date, minute, event.getId(), event.getAggregateType(), event.getData(), event.getTime(), event.getEventtype());
		
		session.execute(bs);

		long total = counter.incrementAndGet();
		if (total % 10000 == 0) {
			logger.info("Total events processed : " + total);
		}
	}	

}
