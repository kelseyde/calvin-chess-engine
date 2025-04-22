#!/bin/sh
exec java --add-modules=jdk.incubator.vector -XX:+UseParallelGC -jar "$0" "$@"