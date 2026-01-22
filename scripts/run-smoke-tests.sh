#!/bin/bash

# Run Smoke Tests Only
# Quick validation of critical functionality

set -e

echo "🚀 Running Smoke Tests..."
echo "=================================="
echo ""

mvn clean test -Dgroups=smoke -B

if [ $? -eq 0 ]; then
    echo ""
    echo "=================================="
    echo "✅ All smoke tests passed!"
    echo "=================================="
    exit 0
else
    echo ""
    echo "=================================="
    echo "❌ Smoke tests failed!"
    echo "=================================="
    exit 1
fi

