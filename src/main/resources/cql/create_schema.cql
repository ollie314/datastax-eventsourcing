create keyspace if not exists datastax WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

use datastax;

CREATE TABLE IF NOT EXISTS eventsource (
date text,
minute int,
id uuid, 
aggregatetype text, 
data text, 
time timestamp, 
eventtype text, 
PRIMARY KEY((date, minute), time, id)
);