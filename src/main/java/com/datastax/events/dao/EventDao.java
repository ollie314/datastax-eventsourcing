package com.datastax.events.dao;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.events.model.Event;

/**
 * @author patrickcallaghan
 *
 */
public class EventDao {

	private static Logger logger = LoggerFactory.getLogger(EventDao.class);
	private Session session;

	private static String keyspaceName = "datastax";
	private static String eventTable = keyspaceName + ".eventsource";

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

	private static final String INSERT_INTO_EVENTS = "insert into " + eventTable + "(date, bucket, id, aggregatetype, host, loglevel, data, time, eventtype) values (?,?,?,?,?,?,?,?,?)";
	private static final String SELECT_BY_DATE = "select * from " + eventTable + " where date =? and bucket = ?"; 
	
	private PreparedStatement insertEvent;
	private PreparedStatement selectByDate;
	private AtomicLong counter = new AtomicLong(0);

	public EventDao(String[] contactPoints) {

		Cluster cluster = Cluster.builder().addContactPoints(contactPoints)
				.withLoadBalancingPolicy(DCAwareRoundRobinPolicy.builder()
						.withUsedHostsPerRemoteDc(3)
						.allowRemoteDCsForLocalConsistencyLevel()
						.build())
				.build();
		
		this.session = cluster.connect();

		this.insertEvent = session.prepare(INSERT_INTO_EVENTS);
		this.selectByDate = session.prepare(SELECT_BY_DATE);
		logger.debug("EventDao created");
	}

	public void insertEvent(Event event) {
		
		DateTime dateTime = new DateTime(event.getTime());

		int bucket = EventDao.getBucket(dateTime);
		String date = formatDate(dateTime);
		
		BoundStatement bs = new BoundStatement(this.insertEvent);
		bs.bind(date, bucket, event.getId(), event.getAggregateType(), event.getHost(), event.getLoglevel(), event.getData(),
				event.getTime(), event.getEventtype());
		
		session.execute(bs);

		long total = counter.incrementAndGet();
		if (total % 10000 == 0) {
			logger.info("Total events processed : " + total);
		}
	}
	
	//Date is not Thread safe
	private synchronized String formatDate(DateTime dateTime) {
		return dateFormatter.format(dateTime.toDate());
	}

	static public int getBucket(DateTime dateTime) {
	
		return dateTime.getMinuteOfDay();
	}

	public void getEventsForDate(BlockingQueue<Event> queue, DateTime time) {
		this.getEventsForDate(queue, time, null);
	}

	public void getEventsForDate(BlockingQueue<Event> queue, DateTime time, String eventType) {
		
		int minute = EventDao.getBucket(time);
		String date = dateFormatter.format(time.toDate());

		ResultSet resultSet = session.execute(selectByDate.bind(date, minute));
		
		//logger.info("Reading for " + date + " and " + minute);
		Iterator<Row> iterator = resultSet.iterator();
		
		while (iterator.hasNext()){
			
			Row row = iterator.next();	
			
			try {
				Event event = rowToEvent(row);
				
				if (eventType == null){
					queue.put(event);					
				}else if (event.getEventtype().equalsIgnoreCase(eventType)){
					queue.put(event);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}

	private Event rowToEvent(Row row) {
		
		Event event = new Event();
		event.setAggregateType(row.getString("aggregatetype"));
		event.setData(row.getString("data"));
		event.setHost(row.getString("host"));
		event.setLoglevel(row.getString("loglevel"));		
		event.setEventtype(row.getString("eventtype"));
		event.setTime(row.getTimestamp("time"));
		event.setId(row.getUUID("id"));
		return event;
	}	

}
