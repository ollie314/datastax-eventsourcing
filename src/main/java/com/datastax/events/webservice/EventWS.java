package com.datastax.events.webservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.zookeeper.Transaction;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.event.model.Event;
import com.datastax.events.service.EventService;

@WebService
@Path("/")
public class EventWS {

	private Logger logger = LoggerFactory.getLogger(EventWS.class);
	private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd");

	//Service Layer.
	private EventService service = new EventService();
	
	@GET
	@Path("/getevents/{from}/{to}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMovements(@PathParam("from") String fromDate, @PathParam("to") String toDate) {
		
		DateTime from = DateTime.now();
		DateTime to = DateTime.now();
		
		try {
			from = new DateTime(inputDateFormat.parse(fromDate));
			to = new DateTime(inputDateFormat.parse(toDate));
		} catch (ParseException e) {
			String error = "Caught exception parsing dates " + fromDate + "-" + toDate;
			
			logger.error(error);
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		BlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(10000);
		
		List<Event> result = service.getEvents(from, to);
		
		return Response.status(Status.OK).entity(result).build();
	}
	
}
