# Example de makefile pour compiler le squelette de code distribué
# Vous pouvez compléter ce makefile, mais étant donnée la taille du projet, 
# il est FORTEMENT recommandé d'utiliser un IDE!

# Organisation:
#  1) Les sources (*.java) se trouvent dans le répertoire src
#     Les classes d'un package toto sont dans src/toto
#     Les classes du package par defaut sont dans src
#
#  2) Les bytecodes (*.class) sont générés dans le répertoire bin
#     La hiérarchie des sources (par package) est conservée.
#
#  3) Une librairie gui.jar est distribuée pour l'interface grapique. 
#     Elle se trouve dans le sous-répertoire lib.
#
# Compilation:
#  Options de javac:
#   -d : répertoire dans lequel sont générés les .class compilés
#   -sourcepath : répertoire dans lequel sont cherchés les .java
#   -classpath : répertoire dans lequel sont cherchées les classes compilées (.class et .jar)

all: runTestInvader 

compileTestInvader:
	javac -d bin -classpath lib/gui.jar src/TestInvader.java

runTestInvader: compileTestInvader
	java -classpath bin:lib/gui.jar TestInvader

clean:
	rm -rf bin/

# ========= Multi-Agents (src/multi_agents) convenience targets =========
# These wrappers let you run the multi-agents Makefile from the repo root.
# They delegate to the Makefile that lives in src/multi_agents/ without
# duplicating compile/run logic.

.PHONY: run-ecosystem run-multi run-events run-boids multi_agents-compile multi_agents-clean multi_agents-help

MA_DIR := src/multi_agents

# Compile all multi_agents sources
multi_agents-compile:
	$(MAKE) -C $(MA_DIR) compile

# Simple boids demo (proies)
run-boids:
	$(MAKE) -C $(MA_DIR) run

# Predators vs prey demo
run-multi:
	$(MAKE) -C $(MA_DIR) run-multi

# Event manager demo
run-events:
	$(MAKE) -C $(MA_DIR) run-events

# Dynamic ecosystem (Lotka-Volterra-like)
run-ecosystem:
	$(MAKE) -C $(MA_DIR) run-ecosystem

# Clean multi_agents compiled classes only
multi_agents-clean:
	$(MAKE) -C $(MA_DIR) clean

# Show help for multi_agents targets
multi_agents-help:
	$(MAKE) -C $(MA_DIR) help

