SRC_PATH := ./src/indi/IPrepo/crawl/
BIN_PATH := ./bin/indi/IPrepo/crawl/
LIB_PATH := ./lib

#LD := -classpath $(LIB_PATH)/dom4j-1.6.1.jar -classpath $(LIB_PATH)/commons-logging-1.2.jar \
#-classpath $(LIB_PATH)/httpclient-4.5.2.jar -classpath $(LIB_PATH)/httpcore-4.4.4.jar\
#-classpath $(LIB_PATH)/jsoup-1.8.3.jar

LD := -cp ./:./bin/:$(LIB_PATH)/dom4j-1.6.1.jar:$(LIB_PATH)/asyn4j-1.3.jar:$(LIB_PATH)/commons-logging-1.2.jar
FLAGS := -d ./bin/ $(LD)

target:
	javac $(FLAGS) $(SRC_PATH)Context.java
	javac $(FLAGS) $(SRC_PATH)Log.java
	javac $(FLAGS) $(SRC_PATH)Asyn_Http.java
	javac $(FLAGS) $(SRC_PATH)Html_Parse.java
	javac $(FLAGS) $(SRC_PATH)Asyn_IP.java

run:
	java $(LD) indi/IPrepo/crawl/Asyn_IP

.PHONY : IPrepo Test
IPrepo:
	java $(LD) com/sina/crawl/IPrepo

Test:
	java $(LD) indi/IPrepo/crawl/Asyn_Http
clean:
	rm  $(BIN_PATH)*.class