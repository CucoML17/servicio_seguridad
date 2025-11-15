FROM eclipse-temurin:17-jre-focal
ARG JAR_FILE=target/authservicio-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app_seguridad.jar
EXPOSE 3013
ENTRYPOINT ["java", "-jar", "app_seguridad.jar"]