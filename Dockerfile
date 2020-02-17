FROM registry.lisong.pub:5000/sunrise/maven:3.6.1-jdk-8-211-jenkins as builder
COPY --chown=1000:1000 . /build/
WORKDIR /build
USER jenkins
RUN mvn package

FROM registry.lisong.pub:5000/sunrise/java:8-jdk-211
VOLUME /tmp
ARG JAR_FILE
COPY --from=builder /build/${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["/run-java.sh"]
