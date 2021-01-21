
#  ${PATH_TO_FX} must be define in shell



JAVA_FILES=$(wildcard *.java)
JAVA_CLASS=$(notdir  $(patsubst %.java, %.class,  $(JAVA_FILES) ) )




NAME=Jixmu
PACK=org/phypo/Jixmu
ROOT=../../..
JAR_PATH=../../../../../Bin
JAR_INSTALL=../../../../../Bin/jixmu


PPG_JAR=${JAR_PATH}/PPgLib.jar



all:: ${JAVA_CLASS}


jar::
	cd ${ROOT}; \
	rm  -f   ${PACK}/${NAME}.jar;  \
	jar cf   ${PACK}/${NAME}.jar  ${PACK}/*.class; \


install::
	mkdir -p ${JAR_INSTALL}; \
	cp ${JAR_PATH}/PPgLib.jar ${JAR_INSTALL};\
	cp -p ${NAME}.jar     ${JAR_INSTALL}; \
	cp -rp ${ROOT}/../Resources  ${JAR_INSTALL}; \
	cp -rp ${ROOT}/../Scripts/*  ${JAR_INSTALL}; \


clean::
	rm -f *.class *.jar



%.class: %.java
	javac     --module-path ${PATH_TO_FX}  --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics,javafx.fxml,javafx.media,javafx.web --add-exports javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAME --add-reads javafx.graphics=ALL-UNNAMED --add-opens javafx.controls/com.sun.javafx.charts=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.iio=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.iio.common=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.css=ALL-UNNAMED --add-opens javafx.base/com.sun.javafx.runtime=ALL-UNNAMED   -classpath ${ROOT}:${PPG_JAR}  -deprecation -g  $*.java


