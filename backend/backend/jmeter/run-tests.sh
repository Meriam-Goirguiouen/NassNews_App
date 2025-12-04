#!/bin/bash
# JMeter Test Runner Script for Linux/Mac
# This script runs JMeter tests in non-GUI mode

# Configuration
JMETER_HOME="${JMETER_HOME:-/opt/apache-jmeter-5.6.3}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEST_PLAN="${SCRIPT_DIR}/nassnews-api-test-plan.jmx"
RESULTS_DIR="${SCRIPT_DIR}/results"
REPORTS_DIR="${SCRIPT_DIR}/reports"

# Check if JMeter is installed
if [ ! -f "${JMETER_HOME}/bin/jmeter" ]; then
    echo "ERROR: JMeter not found at ${JMETER_HOME}"
    echo "Please set JMETER_HOME environment variable to your JMeter installation directory"
    echo "or install JMeter from https://jmeter.apache.org/download_jmeter.cgi"
    exit 1
fi

# Create directories if they don't exist
mkdir -p "${RESULTS_DIR}"
mkdir -p "${REPORTS_DIR}"

# Generate timestamp for unique file names
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Set default parameters (can be overridden via command line)
THREADS=${1:-10}
RAMPUP=${2:-10}
LOOPS=${3:-1}
BASE_URL=${4:-http://localhost:8080}

RESULTS_FILE="${RESULTS_DIR}/test-results_${TIMESTAMP}.jtl"
HTML_REPORT="${REPORTS_DIR}/report_${TIMESTAMP}"

echo "========================================"
echo "JMeter Performance Test Runner"
echo "========================================"
echo "Test Plan: ${TEST_PLAN}"
echo "Threads: ${THREADS}"
echo "Ramp-up: ${RAMPUP} seconds"
echo "Loops: ${LOOPS}"
echo "Base URL: ${BASE_URL}"
echo "Results: ${RESULTS_FILE}"
echo "HTML Report: ${HTML_REPORT}"
echo "========================================"
echo ""

# Run JMeter
"${JMETER_HOME}/bin/jmeter" -n \
    -t "${TEST_PLAN}" \
    -l "${RESULTS_FILE}" \
    -e -o "${HTML_REPORT}" \
    -JbaseUrl="${BASE_URL}" \
    -Jthreads="${THREADS}" \
    -JrampUp="${RAMPUP}" \
    -Jloops="${LOOPS}"

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "Test completed successfully!"
    echo "========================================"
    echo "Results file: ${RESULTS_FILE}"
    echo "HTML Report: ${HTML_REPORT}/index.html"
    echo ""
    
    # Try to open HTML report (works on Mac and some Linux distros)
    if command -v xdg-open > /dev/null; then
        xdg-open "${HTML_REPORT}/index.html" 2>/dev/null
    elif command -v open > /dev/null; then
        open "${HTML_REPORT}/index.html" 2>/dev/null
    fi
else
    echo ""
    echo "========================================"
    echo "Test failed with error code: $?"
    echo "========================================"
    exit 1
fi

