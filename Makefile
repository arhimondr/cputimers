ifndef JAVA_HOME
    $(error JAVA_HOME not set)
endif

INCLUDE= -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux"
CFLAGS=-Wall -Werror -fPIC -shared $(INCLUDE)

.PHONY: all assemble compile prepare clean test

TARGET=java/target
CLASSES=$(TARGET)/classes
HEADERS=$(TARGET)/headers
SO=$(TARGET)/libcputimers.so
JAR=$(TARGET)/cputimers.jar

all: assemble

assemble: compile	
	jar cfv $(JAR) -C $(CLASSES) .

compile: prepare 
	$(JAVA_HOME)/bin/javac -g \
		-sourcepath java/src/ \
		-d $(CLASSES) \
		-h $(HEADERS) \
		-implicit:none \
		-encoding UTF8 \
		java/src/com/arhimondr/cputimers/CpuTimers.java \
		java/src/com/arhimondr/cputimers/TestCpuTimers.java
	gcc $(CFLAGS) -o $(SO) cputimers.c

prepare: clean
	mkdir -p $(CLASSES)
	mkdir -p $(HEADERS)

clean: 
	rm -rf $(TARGET)

test: assemble
	java -Djava.library.path=$(realpath .)/$(TARGET) -cp $(JAR) com.arhimondr.cputimers.TestCpuTimers

