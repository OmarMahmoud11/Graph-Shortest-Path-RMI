package org.example.GSPServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class GSPServiceSequential extends UnicastRemoteObject implements GSPInterface{
    private HashMap<Integer,HashSet<Integer>> graph;
    private HashMap<Integer, Integer> nodesSet;

    public GSPServiceSequential(HashMap<Integer,HashSet<Integer>> graph, HashMap<Integer, Integer> nodesSet) throws RemoteException {
        super();
        this.graph = graph;
        this.nodesSet = nodesSet;
    }

    @Override
    public synchronized List<Integer> readRequest(List<String> batchRequest, String clientId) throws RemoteException, InterruptedException {
        System.out.println("Processing Client:  " + clientId);
        List<Integer> results = new ArrayList<>();

        for(String request : batchRequest){
            String[] requestParts = request.split(" ");
            if(requestParts[0].equals("Q")){
                GSPWorker gspWorker = new GSPWorker(graph, nodesSet, request);
                results.add(gspWorker.call());
            }
            else{
                GSPWorker gspWorker = new GSPWorker(graph, nodesSet, request);
                gspWorker.call();
            }
        }
        System.out.println("End of Processing Client:  " + clientId);

        return results;
    }


}