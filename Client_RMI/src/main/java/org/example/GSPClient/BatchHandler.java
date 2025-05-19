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

    public List<String> generateBatch(int batchSize, int maxNodeId, double addAndDeleteRatio) {
        List<String> batch = new ArrayList<>();
        int numQuery = (int) (batchSize * (1 - addAndDeleteRatio));
        int numAdd = (int) (batchSize * (addAndDeleteRatio * 0.5));
        int numDelete = batchSize - (numAdd + numQuery);

        for (int i = 0; i < numQuery; i++) {
            String op = "Q";
            int start = rand.nextInt(maxNodeId) + 1;
            int end = rand.nextInt(maxNodeId) + 1;
            while (end == start) {
                end = rand.nextInt(maxNodeId) + 1; // avoid self-loop if needed
            }
            batch.add(op + " " + start + " " + end);
        }

        for (int i = 0; i < numAdd; i++) {
            String op = "A";
            int start = rand.nextInt(maxNodeId) + 1;
            int end = rand.nextInt(maxNodeId) + 1;
            while (end == start) {
                end = rand.nextInt(maxNodeId) + 1; // avoid self-loop if needed
            }
            batch.add(op + " " + start + " " + end);
        }

        for (int i = 0; i < numDelete; i++) {
            String op = "D";
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

    public List<List<String>> generateBatches(int numReq, int batchSize, int maxNodeId, double addAndDeleteRatio) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < numReq; i++) {
            List<String> batch = generateBatch(batchSize, maxNodeId, addAndDeleteRatio);
            batches.add(batch);
        }
        return batches;
    }
}
