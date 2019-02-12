package com.inferatus;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MasterImpl extends Master {
    Deque<TabletServer> servers;
    List<Tablet> tablets;

    public MasterImpl(int numTablets, List<String> serverNames) {
        super(numTablets, serverNames);
        servers = new ConcurrentLinkedDeque<>();
        tablets = new ArrayList<>();
        for(String serverName : serverNames) {
            servers.add(new TabletServer(serverName));
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

    }

    @Override
    public void removeServer(String serverName) {

    }

    private void distributeTabletsAcrossServers(List<Tablet> tabletsToAdd) {
        for(Tablet tablet : tabletsToAdd) {
            TabletServer server = servers.removeFirst();
            server.addTablet(tablet);
            servers.addLast(server);
        }
    }
}
