@echo off
@title TWMS
color 0B
set CLASSPATH=.;lib\*
java -server tools.HairFaceDump
pause