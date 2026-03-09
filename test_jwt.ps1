# Simple JWT test
$token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMSIsImVtYWlsIjoibGF0aGFraW5pN0BnbWFpbC5jb20iLCJpYXQiOjE3NzI5OTc2MTMsImV4cCI6MTc3MzA4NDAxM30.uCI8uUEGZ8PI8uQznzNsXemqViENT14wbrdgJXHNOMGonwcgDiWLo6v3hObmIL3UMSkIkHTAgCsoQQoFQz6sig"

Write-Host "Testing JWT Token..."
Write-Host "Token: $($token.Substring(0, 50))..."
Write-Host ""

# Test with token
Write-Host "Making request with token..."
try {
    $response = Invoke-WebRequest `
        -Uri "http://192.168.0.203:8080/api/profile" `
        -Method GET `
        -Headers @{
            "Authorization" = "Bearer $token"
        } `
        -ErrorAction Stop
    
    Write-Host "SUCCESS! Status: $($response.StatusCode)"
    $response.Content | ConvertFrom-Json | Format-List
} catch {
    Write-Host "FAILED! Status: $($_.Exception.Response.StatusCode)"
    Write-Host "Message: $_"
}
