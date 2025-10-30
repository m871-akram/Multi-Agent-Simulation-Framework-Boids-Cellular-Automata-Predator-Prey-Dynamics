# Makefile pour le projet Multi-Agents Boids

# Chemins
BIN_DIR = ../../bin
LIB_DIR = ../../lib
GUI_JAR = $(LIB_DIR)/gui.jar

# Classpath
CLASSPATH = $(GUI_JAR)
RUN_CLASSPATH = $(BIN_DIR):$(GUI_JAR)

# Programmes de test
TEST_BOIDS = multi_agents.tests.TestBoids
TEST_MULTI = multi_agents.tests.TestMultiGroupBoids
TEST_EVENT = multi_agents.tests.TestEventManager

.PHONY: all compile run run-multi run-events clean help

all: compile

compile:
	@echo "=== Compilation du projet Multi-Agents Boids ==="
	@mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) -classpath $(CLASSPATH) core/*.java events/*.java sim/*.java tests/*.java
	@echo "✓ Compilation réussie !"

run: compile
	@echo "=== Test simple (50 proies) ==="
	java -classpath $(RUN_CLASSPATH) $(TEST_BOIDS)

run-multi: compile
	@echo "=== Test multi-groupes (prédateurs vs proies) ==="
	java -classpath $(RUN_CLASSPATH) $(TEST_MULTI)

run-events: compile
	@echo "=== Test du gestionnaire d'événements ==="
	java -classpath $(RUN_CLASSPATH) $(TEST_EVENT)

clean:
	@echo "=== Nettoyage ===" 
	rm -rf $(BIN_DIR)/multi_agents
	@echo "✓ Terminé"

help:
	@echo "════════════════════════════════════════"
	@echo "  Makefile - Multi-Agents Boids"
	@echo "════════════════════════════════════════"
	@echo "Cibles :"
	@echo "  make          - Compile le projet"
	@echo "  make run      - Test simple (proies)"
	@echo "  make run-multi - Test prédateurs vs proies"
	@echo "  make clean    - Nettoie les .class"
	@echo "  make help     - Affiche cette aide"
