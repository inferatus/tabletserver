package com.inferatus;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MasterImpl extends Master {
    Map<String, TabletServer> serverMap;
    Deque<TabletServer> servers;
    List<Tablet> tablets;

    public MasterImpl(int numTablets, List<String> serverNames) {
        super(numTablets, serverNames);
        servers = new ConcurrentLinkedDeque<>();
        serverMap = new ConcurrentHashMap<>();
        tablets = new ArrayList<>();
        for(String serverName : serverNames) {
            TabletServer newServer = new TabletServer(serverName);
            servers.add(newServer);
            serverMap.put(serverName, newServer);
        }

        tablets = Tablet.generateTablets((long)numTablets);
        distributeTabletsAcrossServers(tablets);
    }

    @Override
    public String getServerForKey(long key) {
        for(Tablet tablet : tablets) {
            if(tablet.containsValue(key)) return tablet.getServer().getName();
        }
        //Todo this is an error case.  Throw exception?  Shouldn't be possible to get here
        return null;
    }

    @Override
    public void addServer(String serverName) {
        if(serverMap.containsKey(serverName)) {
            // throw exception. server name should be unique identifier
            return;
        }
        TabletServer server = new TabletServer(serverName);
        while(server.tabletCount() < servers.peekFirst().tabletCount()) {
            TabletServer largestServer = servers.removeLast();
            server.addTablet(largestServer.popTablet());
            servers.addFirst(largestServer);
        }

        servers.addFirst(server);
    }

    @Override
    public void removeServer(String serverName) {
        if(!serverMap.containsKey(serverName)) {
            // throw exception? Can't remove what doesn't exist, but it also doesn't affect run
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
    }

    private void distributeTabletsAcrossServers(List<Tablet> tabletsToAdd) {
        for(Tablet tablet : tabletsToAdd) {
            TabletServer server = servers.removeFirst();
            server.addTablet(tablet);
            servers.addLast(server);
        }
    }
}
