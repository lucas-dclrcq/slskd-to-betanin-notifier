FROM eclipse-temurin:21-jdk


RUN mkdir /opt/app
COPY /target/slskd-to-betanin-notifier-jar-with-dependencies.jar /opt/app/stbn.jar

CMD ["sh", "-c", "java -jar /opt/app/stbn.jar ${COMPLETE_FOLDER_PATH} ${BETANIN_URL} ${BETANIN_API_KEY} ${BETANIN_COMPLETE_FOLDER} ${PUSHOVER_TOKEN} ${PUSHOVER_USER} ${PUSHOVER_DEVICE}"]