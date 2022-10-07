FROM openjdk:11
COPY "./target/registry-service-0.0.1-SNAPSHOT.jar" "app.jar"
EXPOSE 8761
ENTRYPOINT [ "java", "-jar", "app.jar" ]