# 💰 Wallet Service

📄 [Техническое задание](TECHNICAL_TASK)

Приложение для управления пользователями и их кошельками.

## 📄 Описание

Функциональность:

- Аутентификация по email/phone + password (JWT)
- Поиск пользователей с фильтрацией и пагинацией
- Управление контактными данными (email, phone)
- Переводы средств между пользователями
- Автоматическое увеличение баланса каждые 30 секунд (до 207% от начального)
- Кэширование с Redis
- Документация API через Swagger
- Unit и интеграционные тесты с использованием Testcontainers

---

## 📦 Используемый стек

- Java 17
- Spring Boot 3
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Redis
- Swagger (springdoc-openapi)
- Docker + Docker Compose
- JUnit 5 + Mockito + Testcontainers
- Lombok

---

## 🚀 Запуск проекта

### Требования:

- Java 17+
- Maven 3.8+
- Docker + Docker Compose

### 1. Клонирование и сборка:

```bash
git clone https://github.com/Alexiisssss/wallet-service.git
cd wallet-service
./mvnw clean package -DskipTests
```

(Для Windows: `mvnw.cmd clean package -DskipTests`)

### 2. Запуск:

```bash
docker-compose up --build
```

Будут запущены:

- PostgreSQL на `localhost:5432`
- Redis на `localhost:6379`
- Приложение на `http://localhost:8080`

---

## 🧪 Инициализация данных (через SQL)

Войти в контейнер PostgreSQL:

```bash
docker exec -it wallet_postgres psql -U wallet_user -d wallet_db
```

Выполнить SQL:

```sql
INSERT INTO "user" (id, name, date_of_birth, password)
VALUES (1, 'Test User', '1990-01-01', '$2a$10$g9YhzGiWxl9GvEujKlAF1eGjOR7VHL42AefcyiNBn9CtC9ps2gxHy');

INSERT INTO email_data (user_id, email) VALUES (1, 'test@example.com');
INSERT INTO phone_data (user_id, phone) VALUES (1, '+71234567890');
INSERT INTO account (user_id, balance, initial_balance) VALUES (1, 1000.00, 1000.00);
```

---

## 🔐 Аутентификация

JWT токен можно получить так:

```bash
curl -X POST http://localhost:8080/api/auth/token \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "phone": "+71234567890", "password": "password123"}'
```

---

## 💸 Перевод средств

```bash
curl -X POST http://localhost:8080/api/transfer \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"toUserId": 2, "amount": 100.00}'
```

---

## 🔄 Автоувеличение баланса

Каждые 30 секунд все аккаунты получают +10% от текущего баланса, но не более 207% от начального.

---

## 📘 Swagger UI

Документация API:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## ✅ Покрытие тестами

### 🧪 Дополнительные тесты

- `TransferApiMockMvcTest`: проверка успешного перевода и отказа без токена через MockMvc.
- `AuthControllerMockMvcTest`: проверка обработки неверных данных авторизации.
- `TestDataInitializer`: компонент, создающий тестовых пользователей и аккаунты для тестов (используется в `@PostConstruct`).


- `TransferServiceTest`: unit-тесты логики перевода
- `UserControllerIntegrationTest`: тест авторизации через API с использованием Testcontainers

---

## 🧠 Архитектурные решения

- 3-слойная архитектура: контроллеры, сервисы, DAO
- Кэширование через Redis (`@Cacheable`)
- JWT содержит только `userId`, используется фильтр `JwtAuthenticationFilter`
- Трансфер — потокобезопасен: `@Transactional` + `PESSIMISTIC_WRITE`
- Баланс обновляется в фоновом шедулере с ограничением роста до 207%

---

## 🛠️ Примечания

- Пользователи создаются вручную (через миграции или SQL)
- Swagger минимально настроен, без лишнего
- Все проверки email/phone на уникальность реализованы в сервисе
- Валидация входных данных: `@Valid`, `@DecimalMin`, `@NotNull` и ручные проверки в сервисах

---

## 🧑‍💻 Автор

Alexiisssss
