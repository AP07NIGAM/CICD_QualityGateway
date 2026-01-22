#!/bin/bash

# Run Full Regression Test Suite
# Comprehensive testing of all functionality

set -e

echo "🧪 Running Full Regression Test Suite..."
echo "=================================="
echo ""

mvn clean test -Dgroups=regression -B

if [ $? -eq 0 ]; then
    echo ""
    echo "=================================="
    echo "✅ All regression tests passed!"
    echo "=================================="
    echo ""
    echo "📄 View test report: target/surefire-reports/index.html"
    exit 0
else
    echo ""
    echo "=================================="
    echo "❌ Regression tests failed!"
    echo "=================================="
    exit 1
fi

