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

    protected GSPService(HashMap<Integer,HashSet<Integer>> graph) throws RemoteException {
        super();
        this.graph = graph;
    }

    @Override
    public synchronized List<Integer> readRequest(List<String> batchRequest, String clientId) throws RemoteException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Integer>> futures = new ArrayList<>();
        List<Integer> results = new ArrayList<>();

        for(String request : batchRequest){
            String[] requestParts = request.split(" ");
            if(requestParts[0].equals("Q")){
                Future<Integer> future = executor.submit(new GSPWorker(graph, request));
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
                GSPWorker gspWorker = new GSPWorker(graph, request);
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

        return results;
    }


}