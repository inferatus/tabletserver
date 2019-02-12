package com.inferatus;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TabletManagerTest {
    TabletManager manager;
    static final int tabletCount = 4;

    @Before
    public void initialize() {
        manager = new TabletManager(tabletCount);
    }

    @Test
    public void getTabletForKey() {
        Tablet tablet = manager.getTabletForKey(0L);
        assertTrue(tablet.containsValue(0L));
        tablet = manager.getTabletForKey(2305843009213693951L);
        assertTrue(tablet.containsValue(2305843009213693951L));
        tablet = manager.getTabletForKey(2305843009213693952L);
        assertTrue(tablet.containsValue(2305843009213693952L));
        tablet = manager.getTabletForKey(4611686018427387903L);
        assertTrue(tablet.containsValue(4611686018427387903L));
        tablet = manager.getTabletForKey(4611686018427387904L);
        assertTrue(tablet.containsValue(4611686018427387904L));
        tablet = manager.getTabletForKey(6917529027641081855L);
        assertTrue(tablet.containsValue(6917529027641081855L));
        tablet = manager.getTabletForKey(6917529027641081856L);
        assertTrue(tablet.containsValue(6917529027641081856L));
        tablet = manager.getTabletForKey(9223372036854775807L);
        assertTrue(tablet.containsValue(9223372036854775807L));
    }
}