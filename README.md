# backend-core Project
[![Java CI with Spotless](https://github.com/a-sycheva/backend-core/actions/workflows/ci.yml/badge.svg)](https://github.com/a-sycheva/backend-core/actions/workflows/ci.yml)

---

## Технологический стек проекта

### Языки и платформы
- **Java 25 LTS** — основной язык разработки
- **Gradle 8.x** — система сборки (через Gradle Wrapper)

### Инструменты качества кода
- **Checkstyle** — статический анализ стиля кода
    - Конфигурация: `config/checkstyle/checkstyle.xml`
    - Запуск: `./gradlew checkstyleMain`
- **JUnit 5** — фреймворк тестирования
    - Запуск: `./gradlew test`

### CI/CD
- **GitHub Actions** — автоматическая проверка PR
    - Checkstyle на каждый коммит
    - Тесты на каждый коммит
    - Конфигурация: `.github/workflows/`

### Правила кода
- Стиль: Google Java Style Guide (через Checkstyle)
- Коммиты: Conventional Commits (`feat:`, `fix:`, `docs:`)
- Ветки: `feature/DVT-X` для задач, `master` — основная
- Pull Request: обязателен для слияния в master

## 🌿 Правило веток

<pre>
master
feature/BCORE-1
</pre>

---

## Сравнение: new внутри vs Dependency Injection через конструктор

### BAD: Тесная связанность
```java
public class LeadService {
	private final LeadRepository repository = new InMemoryLeadRepository();
}
```          
Проблемы:
- Невозможно подставить mock в тестах
- Невозможно заменить на PostgreSQL без изменения кода
- Скрытая зависимость — не видно что нужно для работы

### GOOD: DI через конструктор
```java
public class LeadService {
    private final LeadRepository repository;

    public LeadService(LeadRepository repository) {
        this.repository = repository;
    }
}
```
Преимущества:

- В тестах передаём mock(LeadRepository.class)
- В production передаём InMemoryLeadRepository
- В будущем передаём JpaLeadRepository (Sprint 7)
- Зависимость явная — видно в конструкторе

---
## Сравнение стеков Servlet vs Spring Boot

### Результаты интеграционного теста

| Метрика | Servlet | Spring Boot | Комментарий |
|---------|---------|-------------|-------------|
| Время старта | ~800 ms | ~3600 ms    | Spring загружает IoC контейнер |
| HTTP 200 на /leads | ✅       | ✅           | Оба работают идентично |
| Количество лидов | 6       | 6           | Данные одинаковые |
| Строк Java кода | ~150    | ~30         | Контраст 5:1 |

### Вывод

Оба стека возвращают идентичные данные, но Spring Boot требует в 5 раз меньше кода за счёт auto-configuration. Trade-off: Spring стартует медленнее из-за инициализации IoC контейнера.

*Данные получены из `StackComparisonTest.java`*


---

# Refactoring Summary: InviteeController


| Метрика | До рефакторинга | После рефакторинга |
|---------|-----------------|--------------------|
| Строк кода в контроллере | 79              | 53                 |
| Количество зависимостей | 1               | 1                  |
| Цикломатическая сложность | 5               | 1                  |
| Проблем категории CRITICAL | 9               | 0                  |
| Проблем категории MAJOR | 7               | 0                  |
| Проблем категории MINOR | 1               | 0                  |

---

## Исправленные проблемы (по категориям)

### API Design
✅ Issue #1: Отсутствие пагинации — добавление pageable в метод репозитория

✅ Issue #2: Неправильные HTTP методы — замена PostMapping на GetMapping

✅ Issue #3:  Entity вместо DTO в response — использование DTO InviteeResponse вместо Entity Invitee

✅ Issue #4:  Entity вместо DTO в response — использование DTO InviteeResponse вместо Entity Invitee

✅ Issue #5: Entity вместо DTO в response — использование DTO InviteeResponse вместо Entity Invitee

✅ Issue #6: Плохой naming: глаголы в URL — заменить "/getInvitees" в URL на "/invitees"

✅ Issue #7: Неправильные статус коды — вернуть правильный статус (Вернуть 201 Created), исключения при сохранении выбрасывать в сервисе и отлавливать глобально

✅ Issue #8: Неправильные статус коды — вернуть правильный статус (Вернуть 200 Ok), исключения при выбрасывать в сервисе и отлавливать глобально

### Security
✅ Issue #9: Нет валидации входных данных — использовать DTO CreateInviteeRequest с @Valid вместо Map<String, Object>

✅ Issue #10: Нет валидации входных данных — использовать DTO UpdateInviteeStatusRequest с @Valid вместо Map<String, String>

✅ Issue #11: SQL injection через конкатенацию — использовать derived-метод вместо native-query

### Error Handling
✅ Issue #12: 500 на бизнес-ошибки вместо 4xx — вернуть правильный статус (Вернуть 200 Ok), бизнес-проверку вынести в сервис, где выбрасывать исключение, отлавливать исключение глобально

✅ Issue #13: Пустые catch блоки — бросать исключение в методе сервиса и отлавливать глобально или тут же логгировать ошибку и выбросить исключение (в зависимости от требуемой логики)

### Code Quality
✅ Issue #14: Field injection через @Autowired — использовать использовать Constructor-based Dependency Injection

✅ Issue #15: Бизнес-логика в контроллере — в контроллере вызывать метод сервиса, в сервисе проводить необходимые проверки и передавать запрос в метод репозитория

✅ Issue #16: Бизнес-логика в контроллере — в контроллере вызывать метод сервиса, в сервисе проводить необходимые проверки и передавать запрос в метод репозитория

✅ Issue #17: Бизнес-логика в контроллере — в контроллере вызывать метод сервиса, в сервисе проводить необходимые проверки и передавать запрос в метод репозитория

---

## Ключевые архитектурные изменения
**1. Введение DTO слоя**
<pre>
До: Entity Invitee возвращался напрямую в response
После: CreateInviteeRequest (input), InviteeResponse (output)
Преимущества: Security (нет exposure internal fields), Flexibility (API контракт независим от Entity)
</pre>
**2. Вынос бизнес-логики в Service**
<pre>
До: Все проверки и логика в контроллере (80 строк метод create)
После: Контроллер только HTTP layer (15 строк), InviteeService содержит business rule
Преимущества: Testability (можно тестировать Service отдельно), Reusability (другие контроллеры могут вызывать Service)
</pre>
**3. GlobalExceptionHandler вместо дублирования try-catch**
<pre>
До: Каждый метод контроллера содержит try-catch с одинаковой логикой
После: Контроллер выбрасывает exceptions, GlobalExceptionHandler обрабатывает централизованно
Преимущества: DRY (код error handling написан один раз), Consistency (все errors имеют единый формат)
</pre>
---

## Применение на собеседованиях

### Что демонстрирует этот рефакторинг:

**Систематический подход:** Использовал чек-лист из 4 категорий вместо хаотичного поиска  
**Знание стандартов:** Ссылки на RFC 7231 (HTTP methods), RFC 7807 (Problem Details), Spring Reference  
**Приоритизация:** Сначала исправил CRITICAL (security vulnerabilities), потом MAJOR, потом MINOR  
**Конкретные решения:** Для каждой проблемы дал код пример исправления, не абстрактный совет  
**Архитектурное мышление:** Не просто "переименовал метод", а "реорганизовал в 3-слойную архитектуру"

### Типичные вопросы интервьюера после код-ревью exercise:

> **Q:** "Какую проблему вы считаете самой критичной?"

**A:** "SQL injection в методе create() через string concatenation. Это CRITICAL security vulnerability которая может привести к data breach. Исправление — использовать PreparedStatement или Spring Data JPA методы с автоматическим экранированием."

> **Q:** "Почему вы вынесли бизнес-логику в Service вместо оставить в контроллере?"

**A:** "Контроллер должен отвечать только за HTTP layer согласно Single Responsibility Principle. Бизнес-логика в контроллере усложняет тестирование (нужны @SpringBootTest вместо простых unit тестов) и переиспользование (другой контроллер или scheduled task не может вызвать эту логику)."

> **Q:** "Что бы вы сделали если автор кода не согласен с вашими замечаниями?"

**A:** "Привёл бы ссылки на официальные стандарты и документацию. Например для проблемы 'POST для чтения данных' сослался бы на RFC 7231 раздел 4.3.3 где явно сказано что POST для создания ресурсов. Если это legacy код с техническими ограничениями — попросил бы автора добавить комментарий объясняющий почему нарушается стандарт."

---

## Чек-лист для следующих код-ревью
После этой практики при ревью любого REST контроллера проверяю:

- [ ] HTTP методы соответствуют семантике (GET=read, POST=create, PUT=update, DELETE=remove)
- [ ] Статус коды корректные (201 для POST, 204 для DELETE, 404 для not found)
- [ ] DTO используются вместо Entity в request/response
- [ ] Bean Validation для входных данных (@Valid, @NotBlank, @Email)
- [ ] НЕТ SQL injection через string concatenation
- [ ] НЕТ exposure внутренних полей (password, version, audit fields)
- [ ] GlobalExceptionHandler обрабатывает ошибки, НЕТ дублирования try-catch
- [ ] Problem Details RFC 7807 для error responses
- [ ] Бизнес-логика в Service слое, НЕ в контроллере
- [ ] Constructor injection вместо field injection
- [ ] Pagination для списковых endpoints
- [ ] Тесты покрывают success cases И edge cases (404, validation errors)

---

## Выводы
Систематический код-ревью через структурированный чек-лист позволяет находить 10+ проблем за 15 минут live coding сессии на собеседовании. Ключевые навыки: категоризация проблем (API Design, Security, Error Handling, Code Quality), приоритизация (CRITICAL → MAJOR → MINOR), аргументация через стандарты (RFC, Spring Reference), конкретные предложения решений с примерами кода.
