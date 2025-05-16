package org.example.GSPServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RequestLogWriter {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void writeLog(
            String clientId,
            String requestType, // "Parallel" or "Unparallel"
            int requestNumber,
            List<String> request,
            List<Integer> result,
            double durationMillis
    ) {
        // Define the folder name
        String folderName = "Slog-" + clientId;
        File logDir = new File(folderName);

        // Auto-create folder if it doesn't exist
        if (!logDir.exists()) {
            if (logDir.mkdirs()) {
                System.out.println("Created log directory: " + folderName);
            } else {
                System.err.println("Failed to create log directory: " + folderName);
                return; // abort logging if directory can't be created
            }
        }

        // Create file name and path
        String filename = String.format("%s%s%s-%d.txt", folderName, File.separator, requestType, requestNumber);


        // Write log content
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Timestamp: " + dtf.format(LocalDateTime.now()) + "\n");
            writer.write("Client ID: " + clientId + "\n");
            writer.write("Request Type: " + requestType + "\n");
            writer.write(String.format("Duration: %.2f ms\n", durationMillis));
            writer.write("Request Number: " + requestNumber + "\n");
            writer.write("Request Size: " + request.size() + "\n");
            // number of Qs in the request
            int numQ = 0;
            for (String r : request) {
                if (r.startsWith("Q")) {
                    numQ++;
                }
            }
            writer.write("Number of Qs: " + numQ + "\n");
            // number of As in the request
            int numA = 0;
            for (String r : request) {
                if (r.startsWith("A")) {
                    numA++;
                }
            }
            writer.write("Number of As: " + numA + "\n");
            // number of Ds in the request
            int numD = 0;
            for (String r : request) {
                if (r.startsWith("D")) {
                    numD++;
                }
            }
            writer.write("Number of Ds: " + numD + "\n");
            writer.write("Request Content:\n");
            for (String r : request) {
                writer.write("  " + r + "\n");
            }
            writer.write("Result:\n");
            // write the request Q and the result
            int i = 0;
            for (String r : request) {
                if (r.startsWith("Q")) {
                    writer.write("  " + r + " -> " + result.get(i) + "\n");
                    i++;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to write log file: " + filename);
            e.printStackTrace();
        }
    }
}
