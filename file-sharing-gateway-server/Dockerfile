# Первый этап
FROM eclipse-temurin:22-jdk-noble as build

LABEL maintainer="Manannikov Anton senioravanti@vk.com"

# Переменная времени сборки
ARG JAR_FILE

COPY ${JAR_FILE} app.jar

# Команда mkdir флаг -p -> создать род. каталог для dependency, если он несуществует
# (команда1 ; команда2 ; ...) :: Subshell. Список команд. Переменные subshell локальны для блока subshell.
# Команда jar операнд -x -> извлечь файлы из jar ; параметр -f -> имя jar архива
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)

# Второй этап
FROM eclipse-temurin:22-jdk-noble

# Монтируем в новый анонимный том каталог для временных файлов
VOLUME /tmp

# Просто путь к каталогу, куда распакован JAR архив.
ARG DEPENDENCY=/target/dependency

# Зависимости сервиса
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
# метаданные проекта, включает MANIFEST.IM, pom.xml файлы.
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
# Классы сервиса
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Команда командного процессора Linux, которая будет выполнена после сборки контейнера. Форма записи exec в виде JSON массива.
ENTRYPOINT ["java","-cp","app:app/lib/*","ru.manannikov.filesharingservice.gatewayserver.FileSharingGatewayApplication"]