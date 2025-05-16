param (
    [Parameter(Mandatory = $true)][int]$numReq,
    [Parameter(Mandatory = $true)][int]$batchSize,
    [Parameter(Mandatory = $true)][int]$maxNodeID,
    [Parameter(Mandatory = $true)][int]$numClients
)

for ($i = 1; $i -le $numClients; $i++) {
    $clientId = $i
    Write-Host "Starting client $clientId..."
    Start-Process "java" -ArgumentList "-jar", "Client_RMI-1.0-SNAPSHOT.jar", "$numReq", "$batchSize", "$maxNodeID", "$clientId"
}
