package com.inferatus;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class MasterImplTest {
    static final String one = "one", two = "two", three = "three", four = "four";
    static final int numInitialTablets = 4, numInitialServers = 3;
    List<String> serverNames;
    MasterImpl master;

    @Before
    public void initialize() {
        serverNames = new ArrayList<>();
        serverNames.add(one);
        serverNames.add(two);
        serverNames.add(three);
        master = new MasterImpl(numInitialTablets, serverNames);
    }

    @Test
    public void testConstructor() {
        assertEquals(numInitialTablets, master.numTablets);
        assertEquals(serverNames, master.serverNames);

        assertEquals(numInitialServers, master.serverMap.keySet().size());
        for(TabletServer server : master.servers) {
            assertTrue(server.tabletCount() == 1 || server.tabletCount() == 2);
        }
    }

    @Test
    public void testGetServerForKey() {
        assertEquals(one, master.getServerForKey(0L));
        assertEquals(two, master.getServerForKey(2305843009213693952L));
        assertEquals(three, master.getServerForKey(4611686018427387904L));
    }

    @Test
    public void testRemoveServer() {
        master.removeServer(one);

        assertEquals(numInitialServers - 1, master.serverMap.keySet().size());
        for(TabletServer server : master.servers) {
            assertEquals(2, server.tabletCount());
            assertNotEquals(one, server.getName());
        }

        for(Tablet tablet : master.manager.getAllTablets()) {
            assertNotEquals(one, tablet.getServer().getName());
        }
    }

    @Test
    public void testAddServer() {
        master.addServer(four);

        assertEquals(numInitialServers + 1, master.serverMap.keySet().size());
        for(TabletServer server : master.servers) {
            assertEquals(1, server.tabletCount());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDuplicateServer() {
        master.addServer(one);
    }

    @Test
    public void removeNonExistantServer() {
        master.removeServer(four);

        assertEquals(numInitialServers, master.serverMap.keySet().size());
        for(TabletServer server : master.servers) {
            assertTrue(server.tabletCount() == 1 || server.tabletCount() == 2);
        }
    }

}