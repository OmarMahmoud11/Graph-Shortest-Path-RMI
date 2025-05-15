package org.example.GSPServer;

import java.io.*;
import java.util.*;

public class GraphLoader {
    public HashMap<Integer, HashSet<Integer>> readGraphFromFile(String filename) throws IOException {
        HashMap<Integer, HashSet<Integer>> graph = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length != 2) continue; // skip malformed lines

            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);

            graph.computeIfAbsent(start, k -> new HashSet<>()).add(end);
//            graph.putIfAbsent(end, new HashSet<>()); // ensure end exists in the map
        }

        reader.close();
        return graph;
    }
}

