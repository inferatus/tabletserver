package com.inferatus;

import java.util.ArrayList;
import java.util.List;

public class Tablet {
    private Long rangeStartInclusive;
    private Long rangeEndInclusive;
    private TabletServer server;

    private Tablet(Long start, Long end) {
        this.rangeStartInclusive = start;
        this.rangeEndInclusive  = end;
        this.server = null;
    }

    public void setServer(TabletServer server) {
        this.server = server;
    }

    public TabletServer getServer() {
        return this.server;
    }

    public Boolean containsValue(Long value) {
        return value >= this.rangeStartInclusive && value <= this.rangeEndInclusive;
    }

    public static List<Tablet> generateTablets(Long numberTablets) {
        return generateTablets(numberTablets, 0L, Long.MAX_VALUE);
    }

    public static List<Tablet> generateTablets(Long numberTablets, Long rangeStart, Long rangeEnd) {
        double range = (double)rangeEnd - rangeStart + 1;
        if( rangeStart < 0L ||
            rangeEnd < rangeStart ||
            numberTablets < 1L ||
            numberTablets > range) {

            throw new IllegalArgumentException("Cannot divide range from " + rangeStart + " to " + rangeEnd +
                    " into " + numberTablets + " tablets");
        }

        Long tabletRange = (long) Math.floor(range / numberTablets);
        List<Tablet> tablets = new ArrayList<>();
        Long start = rangeStart;
        Long end = rangeStart + tabletRange - 1;
        for(int i=0; i < numberTablets - 1; i++) {
            tablets.add(new Tablet(start, end));
            start = end + 1;
            end = start + tabletRange - 1;
        }
        tablets.add(new Tablet(start, rangeEnd));

        return tablets;
    }
}
