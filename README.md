# redis-with-objecthashmapper-and-ttl
Redis as database with unique keys, attribute-name as HashKeys &amp; attribute-value as Value (ObjectHashMapper)

Sample application for Redis usage as database where:
- Key is "CUSTOMER_STORE" + customerId
- HashKey is individual attribute-names of Customer
- Value is individual attribute-values of Customer

Additionally, there is an expiry/TTL set for each Key.
