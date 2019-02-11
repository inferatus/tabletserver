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
}