EXE = calvin-chess-engine
JAR_NAME = calvin-chess-engine-6.1.0-SNAPSHOT.jar
JAR_DEST = calvin-chess-engine.jar

ifndef MAVEN_EXE
	MAVEN_EXE = mvn
endif

all:
ifdef JAVA_HOME
	JAVA_HOME=$(JAVA_HOME) $(MAVEN_EXE) -f ./pom.xml package
else
	$(MAVEN_EXE) -f ./pom.xml package
endif
	cp header.sh $(EXE)
	chmod +x $(EXE)
	cp ./target/$(JAR_NAME) $(JAR_DEST)