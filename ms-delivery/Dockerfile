# Use uma imagem base do Java 17
FROM eclipse-temurin:17-jdk
# Onde o app.jar estará dentro do container
WORKDIR /app
# Copia o .jar que o 'mvn package' gerou para dentro do container
COPY target/*.jar app.jar
# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
