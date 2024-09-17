FROM eclipse-temurin:22-jdk

RUN mkdir /opt/app
COPY /target/slskd-to-betanin-notifier-jar-with-dependencies.jar /opt/app/stbn.jar

CMD ["sh", "-c", "java -jar /opt/app/stbn.jar"]