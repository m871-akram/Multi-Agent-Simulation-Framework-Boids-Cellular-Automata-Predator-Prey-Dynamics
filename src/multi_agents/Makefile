# Makefile pour le projet Multi-Agents Boids

# Chemins
BIN_DIR = ../../bin
LIB_DIR = ../../lib
GUI_JAR = $(LIB_DIR)/gui.jar

# Classpath
CLASSPATH = $(GUI_JAR)
RUN_CLASSPATH = $(BIN_DIR):$(GUI_JAR)

# Programmes de test
TEST_BOIDS = multi_agents.TestTest.TestBoids
TEST_MULTI = multi_agents.TestTest.TestMultiGroupBoids
TEST_EVENT = multi_agents.TestTest.TestEventManager
TEST_ECOSYSTEM = multi_agents.TestTest.TestEcosystem

.PHONY: all compile run run-multi run-events run-ecosystem clean help

all: compile

compile:
	@echo "=== Compilation du projet Multi-Agents Boids ==="
	@mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) -classpath $(CLASSPATH) logic/*.java EvenT/*.java simulation/*.java TestTest/*.java
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

run-ecosystem: compile
	@echo "=== Test écosystème dynamique (Lotka-Volterra) ==="
	java -classpath $(RUN_CLASSPATH) $(TEST_ECOSYSTEM)

clean:
	@echo "=== Nettoyage ===" 
	rm -rf $(BIN_DIR)/multi_agents
	@echo "✓ Terminé"

help:
	@echo "════════════════════════════════════════"
	@echo "  Makefile - Multi-Agents Boids"
	@echo "════════════════════════════════════════"
	@echo "Cibles :"
	@echo "  make              - Compile le projet"
	@echo "  make run          - Test simple (proies)"
	@echo "  make run-multi    - Test prédateurs vs proies"
	@echo "  make run-ecosystem - Écosystème dynamique avec reproduction et mort"
	@echo "  make clean        - Nettoie les .class"
	@echo "  make help         - Affiche cette aide"
