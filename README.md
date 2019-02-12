# tabletserver

This is a practice exercise modeling a key/value store.  
Keys are kept in contiguous blocks and distributed evenly across tablets, 
which are then distributed across tabletServers.

In order to maintain an even distribution of tablets, I've utilized a queue.  
On initialization, this allows for a round robin distribution of tablets by 
popping a server off of the head of the queue, adding a tablet to it, then
pushing the server back to the tail of the queue.  Removing a server from the 
system will then pop each tablet off of that server, and reassign them to the 
other servers following this same pattern (pop head, add tablet, push to tail).
In both of these cases, the movement from head to tail results in a 
queue where the server with the least number of tablets is at the head, and the
one with the most is at the tail. Furthermore they can only be separated by a 
difference of 1.

Addition of a new server requires removing tablets from active servers, and 
adding them to the new server until we are once again in balance.  We achieve 
this by rotating the queue in the opposite direction.  The servers with the most
tablets are always at the tail of the queue. So we pop a server from the tail,
pop a tablet from this server, and then push this server to the head of the queue.
The popped tablet is then added to our new server.  We loop in this fashion until
the new server has the same number of tablets as the server at the head (which we
have just placed there), and then we push the new server to head of the queue.

Internally each server maintains its list of tablets as a queue as well,
so that older tablets are removed first during rebalancing.  The combination of
these queues gives the overall system a preference for moving individual tablets 
as little as possible.  

Based on the background criteria, the getServerForKey method will likely need to 
be as fast as possible. To allow for this, I have stored the tablets in a way that
allows lookup in constant time, and Tablet objects know their hosting server.  

In a real world scenario, the migration of tablets would involve moving data from 
one server to another.  During this migration you would likely want to maintain 
duplicates of the tablet on the original server, so that you can continue accepting
requests to that tablet during the migration.  Only once the data has been completely
migrated would you then swap to route requests to the new server.  I've mimicked this
by doing a late binding of tablets to servers.  That is, we don't delete the old server
from the tablet when we call the method to remove the tablet from the server.  I
instead wait until adding the tablet to its new server to update this pointer. 
Extending this would then involve firing off a copy request, and waiting to add the 
tablet until that request has succeeded.  Crucially though in the interim, any requests 
for that tablet's server will return the original server.

I would note that the current implementation utilizes concurrent queues, as I originally
thought these would be necessary to maintain uptime of the getServerForKey calls
during a rebalance.  However, with the optimization to store the host server directly 
I have bypassed the queues for those calls.  The late binding further protects the 
integrity of that process.  Furthermore, the concurrent queues do not actually protect
us in the case of multiple add/remove calls being issued simultaneously.  So these
can be replaced with their non-concurrent counterparts, and I would add a new queuing
service to enforce a single add/remove action at a time.  
