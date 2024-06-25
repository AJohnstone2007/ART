@echo off

echo Deleting old test results
rd /q/s testResults > nul 2>&1
if exist testResults (
echo ** Failed to delete old test results
goto:eof
)
