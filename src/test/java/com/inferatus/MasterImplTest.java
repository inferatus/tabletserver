package com.inferatus;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class MasterImplTest {
    static final String one = "one", two = "two", three = "three", four = "four";
    List<String> serverNames = List.of(one, two, three);
    MasterImpl master;

    @Before
    public void initialize() {
        master = new MasterImpl(4, serverNames);
    }

    @Test
    public void testConstructor() {
        assertEquals(4, master.numTablets);
        assertEquals(serverNames, master.serverNames);
    }

    @Test
    public void testBasicCase() {
        assertEquals(one, master.getServerForKey(0L));
        assertEquals(two, master.getServerForKey(2305843009213693952L));
        assertEquals(three, master.getServerForKey(4611686018427387904L));


        for(TabletServer server : master.servers) {
            assertTrue(server.tabletCount() == 1 || server.tabletCount() == 2);
        }
    }

    @Test
    public void testRemoveServer() {
        master.removeServer(one);

        for(TabletServer server : master.servers) {
            assertEquals(2, server.tabletCount());
            assertNotEquals(one, server.getName());
        }

        for(Tablet tablet : master.tablets) {
            assertNotEquals(one, tablet.getServer().getName());
        }
    }

}