# Первый этап
FROM maven:3.9.9-eclipse-temurin-21 AS build
LABEL maintainer="Manannikov Anton senioravanti@vk.com"


COPY src /home/app/src/
COPY pom.xml /home/app/
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip=true

# Второй этап
FROM eclipse-temurin:21-jre-noble

COPY --from=build /home/app/target/*.jar /usr/local/lib/eureka-server.jar
EXPOSE 8070
ENTRYPOINT ["java","-jar","/usr/local/lib/eureka-server.jar" ]