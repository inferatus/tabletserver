package com.inferatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabletManager {
    Map<Long, Tablet> tabletMap;
    long numTablets;

    public TabletManager(int tabletsToAdd) {
        this.tabletMap = new HashMap<>();
        List<Tablet> tablets = Tablet.generateTablets((long)tabletsToAdd);
        this.numTablets = tabletsToAdd;
        long position = 0;
        for(Tablet tablet : tablets) {
            this.tabletMap.put(position++, tablet);
        }
    }

    public Tablet getTabletForKey(long key) {
        long position = key % (Long.MAX_VALUE / this.numTablets);
        return this.tabletMap.get(position);
    }

    public Collection<Tablet> getAllTablets() {
        return this.tabletMap.values();
    }

}
