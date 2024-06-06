FROM mcr.microsoft.com/azure-app-service/java:17-java17_20240517

WORKDIR /app

COPY target/myapp.jar /app/myapp.jar

EXPOSE 80

CMD ["java", "-jar", "/app/myapp.jar"]
