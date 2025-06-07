#!/bin/sh
exec java --add-modules=jdk.incubator.vector -XX:+UseG1GC -cp "$0" com.kelseyde.calvin.Application "$@"

exit 1