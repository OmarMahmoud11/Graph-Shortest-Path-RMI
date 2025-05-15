package org.example.GSPClient;

import org.example.GSPServer.GSPInterface;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.util.*;

public class GSPStart {
    static String[] operations = {"Q"};
    static Random rand = new Random();

    static class Result{
        public int clientID;
        public List<String> reqs;
        public List<Integer> res;

        public Result(int clientID, List<String> reqs, List<Integer> res){
            this.clientID = clientID;
            this.reqs = reqs;
            this.res = res;
        }
    }

    public static List<String> generateRequests(int numRequests, int maxNodeId) {
        List<String> requests = new ArrayList<>();

        for (int i = 0; i < numRequests; i++) {
            String op = operations[rand.nextInt(operations.length)];
            int start = rand.nextInt(maxNodeId) + 1;
            int end = rand.nextInt(maxNodeId) + 1;
            while (end == start) {
                end = rand.nextInt(maxNodeId) + 1; // avoid self-loop if needed
            }
            requests.add(op + " " + start + " " + end);
        }

        return requests;
    }

    public static void main(String[] args) {
        try {
            Properties config = new Properties();
            config.load(new FileInputStream("src/main/resources/system.properties"));

            String serviceName = config.getProperty("GSP.service.name");

            GSPInterface server = (GSPInterface) Naming.lookup(serviceName);
            System.out.println(serviceName);


            int numThreads = 1;
            int batchSize = 10000;
            int maxNodeId = 20000;
            List<String> batch = generateRequests(batchSize, maxNodeId);

            List<Result> results = Collections.synchronizedList(new ArrayList<>());
            String clientId = "Writer@" + java.net.InetAddress.getLocalHost().getHostName();

            List<Thread> threads = new ArrayList<>();

            long startTime = System.nanoTime();  // Start time in nanoseconds

            for (int i = 0; i < numThreads; i++) {
                int finalI = i;
                Thread t = new Thread(() -> {
                    try {
                        Result r = new Result(finalI, batch, server.readRequest(batch, String.valueOf(finalI)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                t.start();
                threads.add(t);
            }

            for (Thread t : threads) {
                t.join();  // Wait for all threads to finish
            }
            long endTime = System.nanoTime();    // End time in nanoseconds

            double duration = (endTime - startTime) / 1_000_000.0; // Total time in nanoseconds
            System.out.println("Execution time parallel: " + duration + " ms");

//            ******************************************************************************************************
//            ******************************************************************************************************
//            ******************************************************************************************************
            GSPInterface server2 = (GSPInterface) Naming.lookup("sequential");

            List<Thread> threads2 = new ArrayList<>();

            long startTime2 = System.nanoTime();  // Start time in nanoseconds

            for (int i = 0; i < numThreads; i++) {
                int finalI = i;
                Thread t = new Thread(() -> {
                    try {
                        Result r = new Result(finalI, batch, server2.readRequest(batch, String.valueOf(finalI)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                t.start();
                threads2.add(t);
            }

            for (Thread t : threads2) {
                t.join();  // Wait for all threads to finish
            }
            long endTime2 = System.nanoTime();    // End time in nanoseconds

            double duration2 = (endTime2 - startTime2) / 1_000_000.0; // Total time in nanoseconds
            System.out.println("Execution time Unparallel: " + duration2 + " ms");

//            for(Result res: results){
//                System.out.println("client ID:  " + res.clientID);
//                System.out.println("batch:      " + res.reqs);
//                System.out.println("result:     "+ res.res);
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
