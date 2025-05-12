package org.example.GSPServer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;

public class GSPWorker implements Callable<Integer> {
    private HashMap<Integer, HashSet<Integer>> graph;
    private String request;

    public GSPWorker(HashMap<Integer, HashSet<Integer>> graph, String request) {
        this.graph = graph;
        this.request = request;
    }

    public Integer query(int nodeA,int nodeB){

        return -1;
    }

    public void add(int nodeA,int nodeB){
        if (!graph.containsKey(nodeA)){
            graph.put(nodeA, new HashSet<>());
        }
        graph.get(nodeA).add(nodeB);
    }

    public void delete(int nodeA,int nodeB){
        if(graph.containsKey(nodeA)){
            graph.get(nodeA).remove(nodeB);
            if (graph.get(nodeA).isEmpty()) {
                graph.remove(nodeA);
            }
        }
    }

    @Override
    public Integer call(){
        String[] requestParts = request.split(" ");
        int nodeA = Integer.parseInt(requestParts[1]);
        int nodeB = Integer.parseInt(requestParts[2]);

        int result = -1;
        switch (requestParts[0]){
            case "Q":
                result = query(nodeA, nodeB);
                break;
            case "A":
                add(nodeA, nodeB);
                break;
            case "D":
                delete(nodeA, nodeB);
        }

        return result;
    }

}
