package org.example.GSPServer;

import java.util.*;
import java.util.concurrent.Callable;

public class GSPWorker implements Callable<Integer> {
    private HashMap<Integer, HashSet<Integer>> graph;
    private HashMap<Integer, Integer> nodesSet;
    private String request;

    public GSPWorker(HashMap<Integer, HashSet<Integer>> graph, HashMap<Integer, Integer> nodesSet, String request) {
        this.graph = graph;
        this.nodesSet = nodesSet;
        this.request = request;
    }

    public Integer query(int nodeA,int nodeB){
        if(!graph.containsKey(nodeA) || !nodesSet.containsKey(nodeA) || !nodesSet.containsKey(nodeB)){
            return -1;
        }
        else if(nodesSet.get(nodeA) == 0 || nodesSet.get(nodeB) == 0){
            return -1;
        }
        else if(nodeA == nodeB){
            return 0;
        }

        Queue<Integer> queue = new LinkedList<>();
        HashSet<Integer> visited = new HashSet<>();
        HashMap<Integer,Integer> distance = new HashMap<>();

        queue.add(nodeA);
        visited.add(nodeA);
        distance.put(nodeA,0);

        while(!queue.isEmpty()){
            int current = queue.poll();
            if(current==nodeB){
                return distance.get(current);
            }

            for(int neighbor: graph.getOrDefault(current, new HashSet<>())){
                if(!visited.contains(neighbor)){
                    visited.add(neighbor);
                    queue.add(neighbor);
                    distance.put(neighbor, distance.get(current) + 1);
                }
            }
        }
        return -1;
    }

    public void add(int nodeA, int nodeB){
        if(nodeA == nodeB)
            return;

        if (!graph.containsKey(nodeA)){
            graph.put(nodeA, new HashSet<>());
        }
        if(!graph.get(nodeA).contains(nodeB)){
            nodesSet.put(nodeB, nodesSet.getOrDefault(nodeB, 0) + 1);
            nodesSet.put(nodeA, nodesSet.getOrDefault(nodeA, 0) + 1);
            graph.get(nodeA).add(nodeB);
        }
    }
    // a -> f
    // b
    // a,b,c

    public void delete(int nodeA,int nodeB){
        if(nodeA == nodeB)
            return;

        if(graph.containsKey(nodeA) && graph.get(nodeA).contains(nodeB)){
            graph.get(nodeA).remove(nodeB);
            nodesSet.put(nodeB, nodesSet.get(nodeB) - 1);
            nodesSet.put(nodeA, nodesSet.get(nodeA) - 1);
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
