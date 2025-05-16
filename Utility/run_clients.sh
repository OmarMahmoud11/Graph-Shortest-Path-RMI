#!/bin/bash

# Usage check
if [ "$#" -ne 4 ]; then
  echo "Usage: $0 <numReq> <batchSize> <maxNodeID> <numClients>"
  exit 1
fi

# Input parameters
NUM_REQ=$1
BATCH_SIZE=$2
MAX_NODE_ID=$3
NUM_CLIENTS=$4

# Loop and start each client as a background process
for ((i=1; i<=NUM_CLIENTS; i++))
do
  CLIENT_ID="client_$i_$(date +%s%N)"
  echo "Starting client $CLIENT_ID..."
  java -jar Client_RMI-1.0-SNAPSHOT.jar "$NUM_REQ" "$BATCH_SIZE" "$MAX_NODE_ID" "$CLIENT_ID" &
done

# Optional: Wait for all background jobs to finish
wait
echo "All clients finished."
