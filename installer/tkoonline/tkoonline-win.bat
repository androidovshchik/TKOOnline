powershell -Command "New-NetFirewallRule -DisplayName \"TKOOnline\" -Direction Inbound -Action Allow -Protocol TCP -LocalPort 8080"
start tkonline-win.exe