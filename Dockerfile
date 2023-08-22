FROM eclipse-temurin:17-jdk AS BUILDER
WORKDIR layering
ARG JAR=build/libs/open-erp-accounting-*.jar
COPY ${JAR} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR application
COPY --from=BUILDER layering/dependencies/ ./
COPY --from=BUILDER layering/spring-boot-loader/ ./
COPY --from=BUILDER layering/snapshot-dependencies/ ./
COPY --from=BUILDER layering/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
