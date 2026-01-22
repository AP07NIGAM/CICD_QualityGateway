#!/bin/bash

# Quality Gate - Code Coverage Checker
# This script checks if code coverage meets the minimum threshold

set -e

THRESHOLD=80
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "🔍 Running tests with coverage..."
mvn clean verify -DtestGroups=regression -B

echo ""
echo "📊 Generating coverage report..."
mvn jacoco:report -B

# Extract coverage percentage from JaCoCo CSV report
if [ -f "target/site/jacoco/jacoco.csv" ]; then
    COVERAGE=$(awk -F"," '{ instructions += $4 + $5; covered += $5 } END { print int(100*covered/instructions) }' target/site/jacoco/jacoco.csv)
    
    echo ""
    echo "=================================="
    echo "📈 Code Coverage Report"
    echo "=================================="
    echo "Current Coverage: ${COVERAGE}%"
    echo "Required Threshold: ${THRESHOLD}%"
    echo "=================================="
    echo ""
    
    if [ "$COVERAGE" -lt "$THRESHOLD" ]; then
        echo -e "${RED}❌ QUALITY GATE FAILED${NC}"
        echo -e "${RED}Coverage (${COVERAGE}%) is below threshold (${THRESHOLD}%)${NC}"
        echo ""
        echo "💡 Tips to improve coverage:"
        echo "  1. Add more unit tests for uncovered code"
        echo "  2. Review the HTML report: target/site/jacoco/index.html"
        echo "  3. Focus on critical business logic first"
        echo ""
        exit 1
    else
        echo -e "${GREEN}✅ QUALITY GATE PASSED${NC}"
        echo -e "${GREEN}Coverage (${COVERAGE}%) meets threshold (${THRESHOLD}%)${NC}"
        echo ""
        echo "📄 View detailed report: target/site/jacoco/index.html"
        echo ""
        exit 0
    fi
else
    echo -e "${RED}❌ Error: Coverage report not found${NC}"
    exit 1
fi

