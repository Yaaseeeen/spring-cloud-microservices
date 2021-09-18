FROM openjdk:8-jdk-alpine
EXPOSE 8761
COPY build/libs/gateway-1.0-SNAPSHOT.jar .
RUN apk add --no-cashe bash
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh
ENTRYPOINT ["/wait-for.sh", "registry:8761", "--timeout=70", "--" , "java", "-jar", "gateway-1.0-SNAPSHOT.jar"]
