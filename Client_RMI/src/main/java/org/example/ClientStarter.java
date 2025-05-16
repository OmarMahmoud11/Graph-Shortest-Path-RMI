package org.example;

import org.example.GSPServer.GSPInterface;
import org.example.GSPClient.Result;
import org.example.GSPClient.BatchHandler;
import org.example.GSPClient.RequestLogWriter;
import java.rmi.RemoteException;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.util.*;

public class ClientStarter {

    public static void main(String[] args) {
        try {
            GSPInterface server = (GSPInterface) Naming.lookup("update");
            System.out.println("Parallel service: " + server);

            String[] operations = {"Q", "A", "D"};
            Random rand = new Random();
            BatchHandler batchHandler = new BatchHandler(operations);
            int numReq = rand.nextInt(100) + 1; // Random number of requests between 1 and 100
            int batchSize = rand.nextInt(100) + 1; // Random batch size between 1 and 10
            int maxNodeId = 20000;
            int waitTime = 10000; // in milliseconds
            List<List<String>> batches = batchHandler.generateBatches(numReq, batchSize, maxNodeId);

            //genrate random ID
            String randomId = UUID.randomUUID().toString();
            System.out.println("Random ID: " + randomId);
            List<Result> results = new ArrayList<>();
            String clientId = randomId;

            List<Double> ParallelTimes = new ArrayList<>();
            // Create a batch of requests
            for (List<String> batch : batches) {
                long startTime = System.nanoTime();  // Start time in nanoseconds
                Thread t = new Thread(() -> {
                    try {
                        Result r = new Result(clientId, batch, server.readRequest(batch, clientId));
                        results.add(r);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                // wait for the thread to finish
                t.start();
                t.join();  // Wait for the thread to finish
                // Sleep for a while to simulate the wait time
                long endTime = System.nanoTime();  // End time in nanoseconds
                double duration = (endTime - startTime) / 1_000_000.0;
                ParallelTimes.add(duration);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Write the log to a file
            for (int i = 0; i < results.size(); i++) {
                RequestLogWriter.writeLog(
                        clientId,
                        "Parallel",
                        i + 1,
                        results.get(i).reqs,
                        results.get(i).res,
                        ParallelTimes.get(i)
                );
            }

//            ******************************************************************************************************
//            ******************************************************************************************************
//            ******************************************************************************************************
            GSPInterface server2 = (GSPInterface) Naming.lookup("sequential");
            System.out.println("Sequential service: " + server2);
            
            List<Result> results2 = new ArrayList<>();
            List<Double> UnparallelTimes = new ArrayList<>();
            long startTime2 = System.nanoTime();  // Start time in nanoseconds
            for (List<String> batch : batches) {
                long startTime = System.nanoTime();  // Start time in nanoseconds
                Thread t = new Thread(() -> {
                    try {
                        Result r = new Result(clientId, batch, server2.readRequest(batch, clientId));
                        results2.add(r);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                // wait for the thread to finish
                t.start();
                t.join();  // Wait for the thread to finish
                // Sleep for a while to simulate the wait time
                long endTime = System.nanoTime();  // End time in nanoseconds
                double duration = (endTime - startTime) / 1_000_000.0;
                UnparallelTimes.add(duration);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            // Write the log to a file
            for (int i = 0; i < results2.size(); i++) {
                RequestLogWriter.writeLog(
                        clientId,
                        "Unparallel",
                        i + 1,
                        results2.get(i).reqs,
                        results2.get(i).res,
                        UnparallelTimes.get(i)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
