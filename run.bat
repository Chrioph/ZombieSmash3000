@echo off
REM Kompiliert und startet ZombieSmash3000 (benoetigt JDK 8+).
cd /d "%~dp0"
if exist bin rmdir /s /q bin
mkdir bin
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
if errorlevel 1 (del sources.txt & pause & exit /b 1)
del sources.txt
java -cp "bin;res" dev.codenmore.tilegame.Launcher %*
