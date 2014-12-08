# Example of accessing Cassandra from Java

Uses Dropwizard along with the Datastax Java Driver to show how you can stream large amounts of data out of Cassandra over HTTP.

See the following blog: [Streaming large payloads over HTTP from Cassandra with a small Heap: Java Driver + RX-Java](http://christopher-batey.blogspot.co.uk/2014/12/streaming-large-payloads-over-http-from.html)

Most of the operations are synchronous apart from /events/stream which uses RxJava + JAX-RS StreamingOutput to show async streaming.
