package com.inferatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabletManager {
    Map<Long, Tablet> tabletMap;
    long numTablets;

    public TabletManager(int tabletsToAdd) {
        tabletMap = new HashMap<>();
        List<Tablet> tablets = Tablet.generateTablets((long)tabletsToAdd);
        numTablets = tabletsToAdd;
        long position = 0;
        for(Tablet tablet : tablets) {
            tabletMap.put(position++, tablet);
        }
    }

    public Tablet getTabletForKey(long key) {
        long position = key % (Long.MAX_VALUE / numTablets);
        return tabletMap.get(position);
    }

    public Collection<Tablet> getAllTablets() {
        return tabletMap.values();
    }

}
