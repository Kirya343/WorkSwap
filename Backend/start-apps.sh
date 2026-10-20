#!/bin/bash

set -e

for jar in apps/*/target/*.jar; do
    echo "Starting $jar..."
    java -jar "$jar" &
done

wait