package org.example.GSPServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class GSPServiceSequential extends UnicastRemoteObject implements GSPInterface{
    private HashMap<Integer,HashSet<Integer>> graph;
    private HashMap<Integer, Integer> nodesSet;
    private HashMap<Integer, Integer> nodesReq;

    public GSPServiceSequential(HashMap<Integer,HashSet<Integer>> graph, HashMap<Integer, Integer> nodesSet) throws RemoteException {
        super();
        this.graph = graph;
        this.nodesSet = nodesSet;
        this.nodesReq = new HashMap<>();
    }

    @Override
    public synchronized List<Integer> readRequest(List<String> batchRequest, String clientId) throws RemoteException, InterruptedException {
        System.out.println("Processing Client:  " + clientId);
        // add the clientId to the nodesReq map if it doesn't exist
        if(!nodesReq.containsKey(Integer.parseInt(clientId))){
            nodesReq.put(Integer.parseInt(clientId), 0);
        }
        long startTime = System.nanoTime();
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
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0;
        RequestLogWriter.writeLog(
                clientId,
                "Sequential",
                nodesReq.get(Integer.parseInt(clientId))+1,
                batchRequest,
                results,
                duration
        );
        nodesReq.put(Integer.parseInt(clientId), nodesReq.get(Integer.parseInt(clientId)) + 1);

        return results;
    }


}