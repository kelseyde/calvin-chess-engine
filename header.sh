#!/bin/sh
exec java --add-modules=jdk.incubator.vector -cp "$0" com.kelseyde.calvin.Application "$@"

exit 1