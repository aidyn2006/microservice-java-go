# Используем образ с JRE для запуска
FROM eclipse-temurin:17-jre

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл в образ
COPY target/library-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт приложения
EXPOSE 8080

# Команда для запуска JAR-файла
ENTRYPOINT ["java", "-jar", "app.jar"]
