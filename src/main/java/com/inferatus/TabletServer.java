package com.inferatus;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TabletServer {
    private final String name;
    Deque<Tablet> tablets;

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
        return this.tablets.removeFirst();
    }
}
