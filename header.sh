#!/bin/sh
DIR=$(dirname "$0")
exec java --add-modules=jdk.incubator.vector -XX:+UseParallelGC -jar "$DIR/calvin-chess-engine.jar" "$@"