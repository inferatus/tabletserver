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
        manager = new TabletManager(numTablets);
        servers = new ConcurrentLinkedDeque<>();
        serverMap = new ConcurrentHashMap<>();
        for(String serverName : serverNames) {
            TabletServer newServer = new TabletServer(serverName);
            servers.add(newServer);
            serverMap.put(serverName, newServer);
        }

        distributeTabletsAcrossServers(manager.getAllTablets());
    }

    @Override
    public String getServerForKey(long key) {
        return manager.getTabletForKey(key).getServer().getName();
    }

    @Override
    public void addServer(String serverName) {
        if(serverMap.containsKey(serverName)) {
            throw new IllegalArgumentException("Server name " + serverName + " already exists");
        }

        TabletServer server = new TabletServer(serverName);
        while(server.tabletCount() < servers.peekFirst().tabletCount()) {
            TabletServer largestServer = servers.removeLast();
            server.addTablet(largestServer.popTablet());
            servers.addFirst(largestServer);
        }

        servers.addFirst(server);
        serverMap.put(serverName, server);
    }

    @Override
    public void removeServer(String serverName) {
        if(!serverMap.containsKey(serverName)) {
            // Don't throw exception. Server to remove doesn't exist so allow flow to continue uninterrupted
            return;
        }

        TabletServer server = serverMap.get(serverName);
        servers.remove(server);
        List<Tablet> orphanedTablets = new ArrayList<>();
        Tablet tablet = server.popTablet();
        while(tablet != null) {
            orphanedTablets.add(tablet);
            tablet = server.popTablet();
        }

        distributeTabletsAcrossServers(orphanedTablets);
        serverMap.remove(serverName);
    }

    private void distributeTabletsAcrossServers(Collection<Tablet> tabletsToAdd) {
        for(Tablet tablet : tabletsToAdd) {
            TabletServer server = servers.removeFirst();
            server.addTablet(tablet);
            servers.addLast(server);
        }
    }
}
