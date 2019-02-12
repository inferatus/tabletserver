package com.inferatus;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class TabletTest {

    @Test
    public void generateTablets() {
        testTablets(10L, 0L, 20L);
        testTablets(21L, 0L, 20L);
    }

    @Test
    public void testMaxLong() {
        List<Tablet> tablets = Tablet.generateTablets(4L);
        Tablet tablet = tablets.get(0);
        assertTrue(tablet.containsValue(0L));
        assertTrue(tablet.containsValue(2305843009213693951L));
        tablet = tablets.get(1);
        assertTrue(tablet.containsValue(2305843009213693952L));
        assertTrue(tablet.containsValue(4611686018427387903L));
        tablet = tablets.get(2);
        assertTrue(tablet.containsValue(4611686018427387904L));
        assertTrue(tablet.containsValue(6917529027641081855L));
        tablet = tablets.get(3);
        assertTrue(tablet.containsValue(6917529027641081856L));
        assertTrue(tablet.containsValue(Long.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeTablets() {
        testTablets(-1L, 0L, 20L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void startLargerThanEnd() {
        testTablets(10L, 20L, 0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooManyTablets() {
        testTablets(10L, 0L, 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void positiveNumbersOnly() {
        testTablets(10L, -1L, 20L);
    }

    private void testTablets(long numTablets, long start, long end) {
        List<Tablet> tablets = Tablet.generateTablets(numTablets, start, end);
        assertEquals(numTablets, tablets.size());
        long currentNum = start;
        for(Tablet tablet : tablets) {
            while(tablet.containsValue(currentNum)) {
                assertTrue(currentNum >= start && currentNum <= end);
                currentNum++;
            }
        }
        assertEquals(end + 1, currentNum);
    }
}