param (
    [Parameter(Mandatory = $true)][int]$numReq,
    [Parameter(Mandatory = $true)][int]$batchSize,
    [Parameter(Mandatory = $true)][int]$maxNodeID,
    [Parameter(Mandatory = $true)][int]$numClients,
    [Parameter(Mandatory = $true)][double]$addAndDelRatio
)

for ($i = 1; $i -le $numClients; $i++) {
    $clientId = $i
    Write-Host "Starting client $clientId..."
    Start-Process "java" -ArgumentList "-jar", "Client_RMI-2.0-SNAPSHOT.jar", "$numReq", "$batchSize", "$maxNodeID", "$clientId" ,"$addAndDelRatio"
}
