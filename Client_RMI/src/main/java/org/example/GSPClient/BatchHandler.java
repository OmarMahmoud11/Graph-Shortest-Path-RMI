package org.example.GSPClient;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.util.Properties;    
import java.io.FileInputStream;

public class BatchHandler {
    private String[] operations;
    private Random rand;

    //constructor
    public BatchHandler(String[] operations) {
        this.operations = operations;
        rand = new Random();
    }

    public List<String> generateBatch(int batchSize, int maxNodeId) {
        List<String> batch = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            String op = operations[rand.nextInt(operations.length)];
            int start = rand.nextInt(maxNodeId) + 1;
            int end = rand.nextInt(maxNodeId) + 1;
            while (end == start) {
                end = rand.nextInt(maxNodeId) + 1; // avoid self-loop if needed
            }
            batch.add(op + " " + start + " " + end);
        }
        batch.add("F"); // Add an end marker to the batch
        return batch;
    }

    public List<List<String>> generateBatches(int numReq, int batchSize, int maxNodeId) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < numReq; i++) {
            List<String> batch = generateBatch(batchSize, maxNodeId);
            batches.add(batch);
        }
        return batches;
    }
}
