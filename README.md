# Task Management API

## :pencil2: Описание

Система управления задачами с использованием JWT для аутентификации. Проект включает функционал для регистрации, логина пользователей, работы с задачами и комментариями. Система также поддерживает refresh токены для восстановления доступа.

## Содержание

- [Установка](#установка)
- [Конфигурация](#конфигурация)
- [Запуск приложения](#запуск-приложения)
- [Тестирование](#тестирование)
- [Примечания](#примечания)

---

## :open_file_folder: Установка

1. Клонируйте репозиторий:
```bash
git clone https://github.com/antonlukisha/task-management-system.git
cd task-management-system
```
2. Создайте базу данных PostgreSQL, например:
```sql
CREATE DATABASE task_management;
```
---
## :dart: Конфигурация
1. Настройте конфигурацию подключения к базе данных и JWT в application.properties:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/task_management
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
jwt.secret.key=your_jwt_secret_key
jwt.access.token.expiration=3600000
jwt.refresh.token.expiration=604800000
```
## :office: Запуск приложения
1.1 Запустите приложение через `mvn`:
```bash
mvn spring-boot:run
```
1.2. Запустите приложение через `docker compose`:
```bash
docker-compose up --build
```
2. Приложение будет доступно по адресу `http://localhost:8080`
---
## :triangular_ruler: Тестирование

Для тестирования backend используйте команду:
```bash
mvn test
```
---

## :paperclip: Примечания

- Для более подробной документации и технических решений, смотрите [Swagger API Documentation](http://localhost:8080/swagger-ui).
- Убедитесь, что ваше окружение (например, база данных) настроено корректно перед запуском приложения.

---