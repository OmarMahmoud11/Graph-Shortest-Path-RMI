package org.example.GSPServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GSPService extends UnicastRemoteObject implements GSPInterface{
    private HashMap<Integer,HashSet<Integer>> graph;
    private HashMap<Integer, Integer> nodesSet;
    private HashMap<Integer, Integer> nodesReq;
    private int numThreads;


    public GSPService(HashMap<Integer,HashSet<Integer>> graph, HashMap<Integer, Integer> nodesSet, int numThreads) throws RemoteException {
        super();
        this.graph = graph;
        this.nodesSet = nodesSet;
        this.numThreads = numThreads;
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
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Integer>> futures = new ArrayList<>();
        List<Integer> results = new ArrayList<>();

        for(String request : batchRequest){
            String[] requestParts = request.split(" ");
            if(requestParts[0].equals("Q")){
                Future<Integer> future = executor.submit(new GSPWorker(graph, nodesSet, request));
                futures.add(future);
            }
            else{
                if(!futures.isEmpty()){
                    for(Future<Integer> future: futures){
                        try {
                            results.add(future.get());
                        }
                        catch (ExecutionException e){
                            System.out.printf("ExecutionException: ", e);
                        }
                    }
                    futures.clear();
                }
                GSPWorker gspWorker = new GSPWorker(graph, nodesSet, request);
                gspWorker.call();
            }
        }
        if(!futures.isEmpty()){
            for(Future<Integer> future: futures){
                try {
                    results.add(future.get());
                }
                catch (ExecutionException e){
                    System.out.printf("ExecutionException: ", e);
                }
            }
            futures.clear();
        }
        System.out.println("End of Processing Client:  " + clientId);
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0;
        RequestLogWriter.writeLog(
                clientId,
                "Parallel",
                nodesReq.get(Integer.parseInt(clientId))+1,
                batchRequest,
                results,
                duration
        );
        nodesReq.put(Integer.parseInt(clientId), nodesReq.get(Integer.parseInt(clientId)) + 1);
        return results;
    }


}