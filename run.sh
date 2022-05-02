#!/usr/bin/bash -ex

javac -d bin src/cs1302/game/MinesweeperGame.java
javac -d bin -cp bin src/cs1302/game/MinesweeperDriver.java
java -cp bin cs1302.game.MinesweeperDriver tests/tckathrynne.txt
