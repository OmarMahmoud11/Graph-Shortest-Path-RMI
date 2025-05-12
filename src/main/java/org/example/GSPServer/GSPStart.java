package org.example.GSPServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.*;

public class GSPStart {
    public static HashMap<Integer, Integer> getNodesSet(HashMap<Integer, HashSet<Integer>> graph){
        HashMap<Integer, Integer> nodesSet = new HashMap<>();
        for (Map.Entry<Integer, HashSet<Integer>> entry : graph.entrySet()) {
            int node = entry.getKey();
            HashSet<Integer> neighbors = entry.getValue();

            nodesSet.put(node , nodesSet.getOrDefault(node,0) + neighbors.size());
            for(int n: neighbors){
                nodesSet.put(n , nodesSet.getOrDefault(n,0) + 1);
            }
        }
        return nodesSet;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties config = new Properties();
        config.load(new FileInputStream("/home/omar-mahmoud/DistributedSystems/project/Graph-Shortest-Path-RMI/src/main/resources/system.properties"));

        String host = config.getProperty("GSP.server");
        int rmiPort = Integer.parseInt(config.getProperty("GSP.rmiregistry.port"));
        String serviceName = config.getProperty("GSP.service.name");
        String graphInput = config.getProperty("GSP.graph");

        LocateRegistry.createRegistry(rmiPort);
//            Registry registry = LocateRegistry.getRegistry(rmiPort);
        GraphLoader graphLoader = new GraphLoader();
        HashMap<Integer, HashSet<Integer>> graph = graphLoader.readGraphFromFile(graphInput);
        HashMap<Integer, Integer> nodesSet = getNodesSet(graph);

        GSPService server = new GSPService(graph, nodesSet);
        System.out.println(serviceName);
        Naming.rebind(serviceName, server);

        System.out.println("GSP Server started on " + host + ":" + rmiPort);
    }
}
