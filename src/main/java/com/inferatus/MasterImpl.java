package com.inferatus;

import java.util.List;

public class MasterImpl extends Master {

    public MasterImpl(int numTablets, List<String> serverNames) {
        super(numTablets, serverNames);
    }

    @Override
    public String getServerForKey(long key) {
        return null;
    }

    @Override
    public void addServer(String serverName) {

    }

    @Override
    public void removeServer(String serverName) {

    }
}
