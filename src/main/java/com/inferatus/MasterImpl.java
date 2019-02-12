package com.inferatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MasterImpl extends Master {
    Map<String, TabletServer> serverMap;
    Deque<TabletServer> servers;
    TabletManager manager;

    public MasterImpl(int numTablets, List<String> serverNames) {
        super(numTablets, serverNames);
        this.manager = new TabletManager(numTablets);
        this.servers = new ConcurrentLinkedDeque<>();
        this.serverMap = new ConcurrentHashMap<>();
        for(String serverName : serverNames) {
            TabletServer newServer = new TabletServer(serverName);
            this.servers.add(newServer);
            this.serverMap.put(serverName, newServer);
        }

        distributeTabletsAcrossServers(this.manager.getAllTablets());
    }

    @Override
    public String getServerForKey(long key) {
        return this.manager.getTabletForKey(key).getServer().getName();
    }

    @Override
    public void addServer(String serverName) {
        if(this.serverMap.containsKey(serverName)) {
            throw new IllegalArgumentException("Server name " + serverName + " already exists");
        }

        TabletServer server = new TabletServer(serverName);
        while(server.tabletCount() < this.servers.peekFirst().tabletCount()) {
            TabletServer largestServer = this.servers.removeLast();
            server.addTablet(largestServer.popTablet());
            this.servers.addFirst(largestServer);
        }

        this.servers.addFirst(server);
        this.serverMap.put(serverName, server);
        this.serverNames.add(serverName);
    }

    @Override
    public void removeServer(String serverName) {
        if(!this.serverMap.containsKey(serverName)) {
            // Don't throw exception. Server to remove doesn't exist so allow flow to continue uninterrupted
            return;
        }

        TabletServer server = this.serverMap.get(serverName);
        this.servers.remove(server);
        List<Tablet> orphanedTablets = new ArrayList<>();
        Tablet tablet = server.popTablet();
        while(tablet != null) {
            orphanedTablets.add(tablet);
            tablet = server.popTablet();
        }

        distributeTabletsAcrossServers(orphanedTablets);
        this.serverMap.remove(serverName);
        this.serverNames.remove(serverName);
    }

    private void distributeTabletsAcrossServers(Collection<Tablet> tabletsToAdd) {
        for(Tablet tablet : tabletsToAdd) {
            TabletServer server = this.servers.removeFirst();
            server.addTablet(tablet);
            this.servers.addLast(server);
        }
    }
}
