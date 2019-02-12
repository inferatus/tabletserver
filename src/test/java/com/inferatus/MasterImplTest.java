package com.inferatus;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class MasterImplTest {

    @Test
    public void testConstructor() {
        List<String> serverNames = List.of("one", "two", "three");
        Master basicMaster = new MasterImpl(3, serverNames);
        assertEquals(3, basicMaster.numTablets);
        assertEquals(serverNames, basicMaster.serverNames);
    }

    @Test
    public void testBasicCase() {
        List<String> serverNames = List.of("one", "two", "three");
        Master master = new MasterImpl(4, serverNames);

        assertEquals("one", master.getServerForKey(0L));
        assertEquals("two", master.getServerForKey(2305843009213693952L));
        assertEquals("three", master.getServerForKey(4611686018427387904L));
    }
}