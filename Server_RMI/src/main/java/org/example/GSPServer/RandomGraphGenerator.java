package org.example.GSPServer;

import java.io.*;
import java.util.*;

public class RandomGraphGenerator {
    public static void main(String[] args) {
        int n = 20000;  // number of nodes
        int m = 30000;  // number of edges

        Set<String> edgeSet = new HashSet<>();
        Random rand = new Random();

        while (edgeSet.size() < m) {
            int u = rand.nextInt(n) + 1;
            int v = rand.nextInt(n) + 1;

            if (u == v) continue; // no self-loops

            // Avoid duplicates (e.g., 1-2 and 2-1 are considered the same)
            String edge = u < v ? u + " " + v : v + " " + u;

            edgeSet.add(edge);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/home/omar-mahmoud/DistributedSystems/project/Graph-Shortest-Path-RMI/input_graph.txt"))) {
            for (String edge : edgeSet) {
                writer.write(edge);
                writer.newLine();
            }
            System.out.println("Graph written to graph.txt");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
