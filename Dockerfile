FROM openjdk:19
EXPOSE 8080
EXPOSE 81
EXPOSE 82
EXPOSE 83
ADD target/dns-manager.jar dns-manager.jar
ENTRYPOINT ["java", "-jar", "/dns-manager.jar"]