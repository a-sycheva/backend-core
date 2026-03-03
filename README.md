# backend-core Project
[![Java CI with Jacoco and Checkstyle](https://github.com/a-sycheva/backend-core/actions/workflows/ci.yml/badge.svg)](https://github.com/a-sycheva/backend-core/actions/workflows/ci.yml)

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