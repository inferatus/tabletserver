package com.inferatus;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TabletServerTest {
    TabletServer server;
    static final String name = "name";

    @Before
    public void initialize() {
        server = new TabletServer(name);
    }

    @Test
    public void getName() {
        assertEquals(name, server.getName());
    }

    @Test
    public void addTablet() {
        List<Tablet> tablets = Tablet.generateTablets(4L);

        for (Tablet tablet : tablets) {
            server.addTablet(tablet);
            assertEquals(name, tablet.getServer().getName());
        }
    }

    @Test
    public void popTablet() {
        List<Tablet> tablets = Tablet.generateTablets(4L);

        for (Tablet tablet : tablets) {
            server.addTablet(tablet);
        }

        for (Tablet tablet : tablets) {
            Tablet tabletFromServer = server.popTablet();
            assertEquals(tablet, tabletFromServer);
            // We only change the server of a tablet on add, so that calls will go to old server until swap finishes
            assertEquals(name, tablet.getServer().getName());
        }
    }
}