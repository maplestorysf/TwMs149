@echo off
@title TWMS
color 0A
set CLASSPATH=.;lib\*
java -server server.Start
pause