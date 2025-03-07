# Используем базовый образ с Java 21
FROM eclipse-temurin:21-jdk-jammy

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем только файлы, необходимые для сборки (оптимизация кэширования)
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# Устанавливаем Gradle (если не используется встроенный)
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.5-bin.zip -O /tmp/gradle.zip && \
    unzip /tmp/gradle.zip -d /opt && \
    ln -s /opt/gradle-8.5/bin/gradle /usr/bin/gradle && \
    gradle --version

# Собираем проект с помощью Gradle
RUN gradle build -x test

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "build/libs/auth-0.0.1-SNAPSHOT.jar"]