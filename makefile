JFLAGS = -g
JC = javac
#JC = /usr/java/jdk1.8.0_45/bin/javac
JVM= java

BIN = ./exe/

.SUFFIXES: .java .class

.java.class:
	#$(JC) $(JFLAGS) $*.java -cp $(CCPATHS) -d $(BIN)
	#$(JC) $(JFLAGS) $*.java -cp $(CCPATHS) -encoding utf-8 -d $(BIN)
	$(JC) $(JFLAGS) $*.java -cp $(CCPATHS) -encoding iso-8859-1 -d $(BIN)

CCPATHS = ./src:$(JARS)
RCPATHS = $(BIN):$(JARS)

CLASSES = \
	src/Main.java \
	src/domain/classes/*.java \
	src/domain/classes/restrictions/*.java \
	src/domain/controllers/*.java \
	src/domain/drivers/*.java \
	src/domain/junits/*.java \
	src/persistance/*.java

JARS = ./externalClasses/json-simple-1.1c.jar

MAIN = Main

default: bin classes

bin:
	mkdir -p $(BIN)

classes: $(CLASSES:.java=.class)

run: $(BIN)$(MAIN).class
	$(JVM) -cp $(RCPATHS) $(MAIN)

run-driver-subject:
	$(JVM) -cp $(RCPATHS) domain.drivers.SubjectDriver

run-driver-group:
	$(JVM) -cp $(RCPATHS) domain.drivers.GroupDriver

run-driver-room:
	$(JVM) -cp $(RCPATHS) domain.drivers.RoomDriver

run-driver-lecture:
	$(JVM) -cp $(RCPATHS) domain.drivers.LectureDriver

run-driver-schedule:
	$(JVM) -cp $(RCPATHS) domain.drivers.ScheduleDriver

run-driver-posAssig:
	$(JVM) -cp $(RCPATHS) domain.drivers.PosAssigDriver

run-driver-ctrlDomain:
	$(JVM) -cp $(RCPATHS) domain.drivers.CtrlDomainDriver

run-driver-ctrlSchedule:
	$(JVM) -cp $(RCPATHS) domain.drivers.CtrlScheduleDriver

run-driver-restriction:
	$(JVM) -cp $(RCPATHS) domain.drivers.RestrictionDriver

run-driver-junit:
	$(JVM) -cp $(RCPATHS) domain.junits.TestingAll


clean:
	$(RM) -rf $(BIN)*
