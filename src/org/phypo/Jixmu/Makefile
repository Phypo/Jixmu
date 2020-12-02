
#  ${PATH_TO_FX} must be define in shell



JAVA_FILES=$(wildcard *.java)
JAVA_CLASS=$(notdir  $(patsubst %.java, %.class,  $(JAVA_FILES) ) )




NAME=JizMapFX
PACK=com/sesame_it/JizMapFX
ROOT=../../..
JAR_PATH=../../../../../Bin


MARIADB_JAR=${JAR_PATH}/mariadb-java-client-2.7.0.jar
PPG_JAR=${JAR_PATH}/PPgLib.jar



all:: ${JAVA_CLASS}


jar::
	cd ${ROOT}; \
	rm  -f   ${PACK}/${NAME}.jar;  \
	jar cf   ${PACK}/${NAME}.jar  ${PACK}/*.class; \


install::
	cp -p ${NAME}.jar     ${JAR_PATH}; \
	cp -rp ${ROOT}/../Resources  ${JAR_PATH}; \
	cp -rp ${ROOT}/../Scripts/*  ${JAR_PATH}; \


clean::
	rm -f *.class *.jar


%.class: %.java
	javac    --module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml  -classpath ${ROOT}:${MARIADB_JAR}:${PPG_JAR}  -deprecation -g  $*.java


