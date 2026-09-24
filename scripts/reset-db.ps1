Write-Host "Resetting ReForgePC database..." -ForegroundColor Cyan

Get-Content .\src\main\resources\db\reset.sql |
    docker exec -i reforgepc-mysql mysql -ureforgepc -preforgepc_dev reforgepc

if ($LASTEXITCODE -ne 0) {
    Write-Host "Database reset failed." -ForegroundColor Red
    exit 1
}

Write-Host "Seeding database..." -ForegroundColor Cyan

Get-Content .\src\main\resources\db\seed.sql |
    docker exec -i reforgepc-mysql mysql -ureforgepc -preforgepc_dev reforgepc

if ($LASTEXITCODE -ne 0) {
    Write-Host "Database seeding failed." -ForegroundColor Red
    exit 1
}

Write-Host "Database reset and seed completed successfully." -ForegroundColor Green