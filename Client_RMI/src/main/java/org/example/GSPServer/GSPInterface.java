package org.example.GSPServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GSPInterface extends Remote{
    public List<Integer> readRequest(List<String> batchRequest, String clientId) throws RemoteException, InterruptedException;
}
