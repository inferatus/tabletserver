package com.inferatus;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TabletServer {
    private final String name;
    private Deque<Tablet> tablets;

    public TabletServer(String name) {
        this.name = name;
        this.tablets = new ConcurrentLinkedDeque<>();
    }

    public String getName() {
        return this.name;
    }

    public void addTablet(Tablet tablet) {
        this.tablets.addLast(tablet);
        tablet.setServer(this);
    }

    public Tablet popTablet() {
        if(tablets.size() == 0) return null;

        return this.tablets.removeFirst();
    }

    public int tabletCount() {
        return tablets.size();
    }
}
