CREATE KEYSPACE customers WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };



CREATE TABLE customer_events( customer_id text, staff_id text, time timeuuid, store_type text, event_type text, tags map<text, text>,  PRIMARY KEY ((customer_id), time));


create TYPE store (name text, type text, postcode text) ;


CREATE TABLE customer_events_type( customer_id text, staff_id text, time timeuuid, store frozen<store>, event_type text, tags map<text, text>,  PRIMARY KEY ((customer_id), time));
