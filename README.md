# Простой Файлообменник

Стек: 
- Язык программирования: Java с исп. фрейморков Spring Boot, Spring Cloud 
- Технологии: Docker, PostgreSQL,  Liquibase, JWT

[Репозиторий с конфигурацией проекта](https://github.com/Antonio-Stradiavanti/spring-mvc-file-sharing-service-config.git)

## Скриншоты

Список контейнеров
![image](./images/containers.png)

Ответ сервера на аутентификацию
![image](./images/authentication.png)

Ответ сервера на загрузку файла в объектное хранилище
![image](./images/upload-file.png)

Получим список файлов, фильтр шлюза извлекает из полезной нагрузки (claims) токена claim subject, то есть имя пользователя и добавляет его к пути запроса.
![image](./images/uploaded-files.png)
Преобразованный путь:
![image](./images/modified-path.png)
Фильтры:
![image](./images/routes-and-filters.png)

Список бакетов
![image](./images/bucket-list.png)