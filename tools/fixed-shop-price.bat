@echo off
@title Dump
color 0B
set CLASSPATH=.;lib\*
java -server tools.FixShopItemsPrice
pause