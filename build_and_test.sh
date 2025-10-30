#!/bin/bash

# Multi-Agent System - Compilation and Test Script

echo "=========================================="
echo "Multi-Agent System Build & Test"
echo "=========================================="
echo ""

# Clean old compiled files
echo "🧹 Cleaning old compiled files..."
rm -rf bin/multi_agents/*.class 2>/dev/null

# Compile all multi_agents packages
echo "🔨 Compiling multi_agents package..."
javac -d bin -classpath lib/gui.jar:bin \
  src/multi_agents/EvenT/*.java \
  src/multi_agents/logic/*.java \
  src/multi_agents/simulation/*.java \
  src/multi_agents/TestTest/*.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
else
    echo "❌ Compilation failed!"
    exit 1
fi

echo ""
echo "=========================================="
echo "Running Tests"
echo "=========================================="
echo ""

# Test 1: EventManager
echo "📋 Test 1: EventManager (PING/PONG)"
echo "-----------------------------------"
java -classpath bin:lib/gui.jar multi_agents.TestTest.TestEventManager
echo ""

# Test 2: Single Group Boids
echo "📋 Test 2: Single Boid Group (GUI)"
echo "-----------------------------------"
echo "Starting single boid group simulation..."
echo "Press Ctrl+C after viewing to stop"
java -classpath bin:lib/gui.jar multi_agents.TestTest.TestBoids &
BOID_PID=$!
sleep 3
echo "Process ID: $BOID_PID"
echo ""

# Test 3: Multi-Group Boids
echo "📋 Test 3: Multi-Group Boids - Predator/Prey (GUI)"
echo "-----------------------------------"
echo "Starting predator-prey simulation..."
echo "Press Ctrl+C after viewing to stop"
java -classpath bin:lib/gui.jar multi_agents.TestTest.TestMultiGroupBoids &
MULTI_PID=$!
sleep 3
echo "Process ID: $MULTI_PID"
echo ""

echo "=========================================="
echo "✅ All tests launched successfully!"
echo "=========================================="
echo ""
echo "GUI simulations are running in the background."
echo "Close the windows to terminate them."
echo ""
echo "Architecture summary:"
echo "  📦 multi_agents.core      - Boid agents & rules"
echo "  📦 multi_agents.events    - Event system"
echo "  📦 multi_agents.sim       - Simulators"
echo "  📦 multi_agents.tests     - Test programs"
