
## Issue #1: Отсутствие пагинации

**Категория:** API Design
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 26, метод getInvitees

**Что плохо:**
```java
  @PostMapping("/getInvitees")
  public List<Invitee> getInvitees() {
    return repository.findAll();
  }
```

**Почему плохо:**
Клиентский API обычно ожидает часть данных, а неожиданно для себя получает все. При большом количестве возвращаемых записей получаем также большую нагрузку на инфраструктуру.

**Как исправить:**
```java
  @PostMapping("/getInvitees")
  public Page<Invitee> getInvitees(Pageable pageable) {
    return repository.findAll(pageable);
  }
```

---
## Issue #2: Неправильные HTTP методы

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 24, метод getInvitees

**Что плохо:**
```java
  @PostMapping("/getInvitees")
  public List<Invitee> getInvitees() {
    return repository.findAll();
  }
```

**Почему плохо:**
Нарушение семантического соответствия HTTP-методов (RFC 7231). Может привести к непреднамеренным действиям, неожиданным результатам выполнения запроса. Также вводит в заблуждение разработчиков API.

**Как исправить:**
использовать GET-метод https://www.rfc-editor.org/rfc/rfc7231#section-4.3.1
```java
  @GetMapping("/getInvitees")
  public List<Invitee> getInvitees() {
    return repository.findAll();
  }
```
---
## Issue #3: Entity вместо DTO в response

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 25, метод getInvitees

**Что плохо:**
```java
  @PostMapping("/getInvitees")
  public List<Invitee> getInvitees() {
    return repository.findAll();
  }
```

**Почему плохо:**
Возможна утечка внутренних полей, что нарушает безопасность. Также возможны проблемы при наличии у сущности связей один-ко-многим (необходимость подгрузки и сериализации связанной сущности, увеличение размера json-ответа, N+1 и т.п.)  

**Как исправить:**
```java
  @PostMapping("/getInvitees")
  public List<InviteeResponse> getInvitees() {
    return repository.findAll().map(InviteeMapper::toResponse);
  }
```

---

## Issue #4: Entity вместо DTO в response

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 31, метод getById

**Что плохо:**
```java
  @GetMapping("/invitees/{id}")
public Invitee getById(@PathVariable UUID id) {
  return repository.findById(id).orElse(null);
}
```

**Почему плохо:**
Возможна утечка внутренних полей, что нарушает безопасность. Также возможны проблемы при наличии у сущности связей один-ко-многим (необходимость подгрузки и сериализации связанной сущности, увеличение размера json-ответа, N+1 и т.п.)

**Как исправить:**
```java
  @GetMapping("/invitees/{id}")
public InviteeResponse getById(@PathVariable UUID id) {
  return repository.findById(id).map(InviteeMapper::toResponse).orElse(null);
}
```
---

## Issue #5: Entity вместо DTO в response

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 37, метод create

**Что плохо:**
```java
 @PostMapping("/invitees")
public Invitee create(@RequestBody Map<String, Object> params) {
  
  ...

  return repository.save(invitee);
}
```

**Почему плохо:**
Возможна утечка внутренних полей, что нарушает безопасность. Также возможны проблемы при наличии у сущности связей один-ко-многим (необходимость подгрузки и сериализации связанной сущности, увеличение размера json-ответа, N+1 и т.п.)

**Как исправить:**
```java
 @PostMapping("/invitees")
public InviteeResponse create(@RequestBody Map<String, Object> params) {
  
  ...

  return repository.save(invitee).map(InviteeMapper::toResponse);
}
```
---

## Issue #6: Плохой naming: глаголы в URL

**Категория:** API Design
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 24, метод getInvitees

**Что плохо:**
```java
  @PostMapping("/getInvitees")
```

**Почему плохо:**
Нарушение REST (единообразие интерфейсов) - взаимодействие с ресурсами должно быть стандартизировано через HTTP методы, а не через имена действий в URL.

**Как исправить:**
```java
  @PostMapping("/invitees")
```

---

## Issue #7: Неправильные статус коды

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 51, метод create

**Что плохо:**
```java
  public Invitee create(@RequestBody Map<String, Object> params) {

  ...

  return repository.save(invitee);
}
```

**Почему плохо:**
Статус 200 при успешном или не успешном создании сущности, нарушается семантика HTTP (RFC 7231)

**Как исправить:**
Вернуть 201 Created
```java
  @PostMapping("/invitees")
public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) {
  InviteeResponse created = service.save(request);
  URI location = URI.create("/api/invitees/" + created.id());
  return ResponseEntity.created(location).body(created);
}
```
исключения при сохранении выбрасывать в сервисе
```java
  public InviteeResponse save(CreateInviteeRequest request) {
    if (repository.findByEmail(request.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(request.getEmail());
    }
    Invitee invitee = mapper.toEntity(request);
    return  mapper.toResponse(repository.save(invitee));
  }
```
отлавливать и обрабатывать исключения глобально
```java
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex, WebRequest request) {
    ErrorResponse errorResponse =  new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.CONFLICT.value(), // 409 Conflict (бизнес-ошибка, не server error)
        HttpStatus.CONFLICT.getReasonPhrase(), // 409 Conflict (бизнес-ошибка, не server error)
        ex.getMessage(),
        request.getDescription(false).substring(4)
    );
    return ResponseEntity.status(409).body(errorResponse);
  }
```
---

## Issue #8: Неправильные статус коды

**Категория:** Error Handling
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 32, метод getById

**Что плохо:**
```java
  public Invitee getById(@PathVariable UUID id) {
  return repository.findById(id).orElse(null);
}
```

**Почему плохо:**
HTTP статус код должен соответствовать категории ошибки согласно RFC 7231. Нарушение этого правила усложняет клиенту обработку случая.

**Как исправить:**
вернуть 200 OK
```java
  @GetMapping("/invitees/{id}")
public ResponseEntity<Invitee> getById(@PathVariable UUID id) {
  return ResponseEntity.ok().body(service.findById(id));
}
```
в сервисе осуществить проверку и выбросить кастомное исключение
```java
  public Invitee findById(UUID id) {
    return repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Invitee", id.toString()));
  }
```
отловить и обработать его в GlobalHandler
```java
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFound(
      EntityNotFoundException ex, WebRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).substring(4));

    LOG.warn("Entity not found: " + ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
```
---

## Issue #9: Нет валидации входных данных

**Категория:** Security
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 37, метод create

**Что плохо:**
```java
 @PostMapping("/invitees")
public Invitee create(@RequestBody Map<String, Object> params) {

```

**Почему плохо:**
Нет валидации и типизации, может быть передано что угодно. Непредсказуемое поведение.

**Как исправить:**
использовать DTO
```java
public Invitee create(@Valid @RequestBody CreateInviteeRequest request) {
```
---

## Issue #10: Нет валидации входных данных

**Категория:** Security
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 66, метод updateStatus

**Что плохо:**
```java
public Invitee updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {

```

**Почему плохо:**
Нет валидации и типизации, может быть передано что угодно. Непредсказуемое поведение.

**Как исправить:**
использовать DTO
```java
public Invitee updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateInviteeStatusRequest request) {
```
---

## Issue #11: SQL injection через конкатенацию

**Категория:** Security
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 42, метод create

**Что плохо:**
```java
    String sql = "SELECT COUNT(*) FROM invitees WHERE email = '" + email + "'";
    repository.executeNativeQuery(sql);
```

**Почему плохо:**
Пользовательский ввод напрямую конкатенируется в SQL запрос, злоумышленник может передать под видом параметра свой SQL-запрос и получить непредсказуемое поведение (пепредать под видом email строку вида admin@test.com' OR '1'='1 и получить доступ ко всем записям)

**Как исправить:**
использовать PreparedStatement с параметрами или Spring Data JPA методы:
```java
// Spring Data JPA method  
repository.findByEmail(email); // Автоматическое экранирование  
  
// Или PreparedStatement  
PreparedStatement ps = conn.prepareStatement("SELECT * FROM invitees WHERE email = ?");  
ps.setString(1, email); //параметры никогда не превратятся в SQL
```
---

## Issue #12: 500 на бизнес-ошибки вместо 4xx

**Категория:** Error Handling
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 75, метод updateStatus

**Что плохо:**
```java
      if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
        invitee.setStatus(status);
      } else {
        throw new RuntimeException("Invalid status");
      }
```

**Почему плохо:**
HTTP статус код должен соответствовать категории ошибки согласно RFC 7231. Нарушение этого правила усложняет клиенту обработку случая. 

**Как исправить:**
вернуть 200 ОК
```java
  @PutMapping("/invitees/{id}/status")
public ResponseEntity<InviteeResponse> updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateInviteeStatusRequest request) {

  InviteeResponse updated = service.updateStatus(id, request);

  return ResponseEntity.ok().body(updated);
}
```
Бизнес-проверку вынести в сервис, выбрасывать кастомное исключение
```java
  public InviteeResponse updateStatus(UUID id, UpdateInviteeStatusRequest request) {
  Invitee invitee = repository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Invitee", id.toString()));

  if (!request.getStatus().toString().equals("ACTIVE") && !request.getStatus().toString().equals("INACTIVE")) {
    throw new InvalidStatusException(request.getStatus().toString());
  }

  mapper.updateEntity(request, invitee);
  return mapper.toResponse(repository.save(invitee));
}
```
Отлавливать исключение глобально
```java
@ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFound(
      EntityNotFoundException ex, WebRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).substring(4));

    LOG.warn("Entity not found: " + ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
```

---

## Issue #13: Пустые catch блоки

**Категория:** Error Handling
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 81, метод updateStatus

**Что плохо:**
```java
    try {
    ...
    } catch (Exception e) {
      // Пустой catch
      return null;
    }
```

**Почему плохо:**
Система должна падать быстро и явно, а не продолжать работу с повреждённым состоянием. Пустые try-catch скрывают проблемы, усложняют debugging и возвращает клиенту некорректные данные.

**Как исправить:**
Можно логгировать ошибку и выбросить исключение
```java
    try {
    ...
    } catch (Exception e) {
      log.error("Failed to update status for invitee {}", id, e);
       throw new ServiceException("Update failed", e);
    }
```

---

## Issue #14: Field injection через @Autowired

**Категория:** Code Quality
**Приоритет:** MINOR
**Местоположение:** InviteeController.java, строка 21

**Что плохо:**
```java
  @Autowired
  InviteeRepository repository;
```
**Почему плохо:**
Field injection усложняет тестирование (нельзя передать mock через конструктор) и делает зависимости неявными.

**Как исправить:**
использовать Constructor-based Dependency Injection (вместо конструктора можно использовать lombok-аннотацию @RequiredArgsConstructor):
```java
private final InviteeRepository repository;

  public InviteeController(InviteeRepository repository) {
    this.repository = repository;
  }
```
или Setter-based Dependency Injection
https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html

---
## Issue #15: Бизнес-логика в контроллере

**Категория:** Code Quality
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 43, метод create

**Что плохо:**
```java
    String sql = "SELECT COUNT(*) FROM invitees WHERE email = '" + email + "'";
    repository.executeNativeQuery(sql);
```

**Почему плохо:**
Нарушение Single Responsibility Principe - контроллер не должен отвечать за работу с репозиторием, проверка дублирования email и прочих бизнес-ограничений это задача сервиса.

**Как исправить:**
В контроллере вызывать сохранение сущности через сервис
```java
return service.save(invitee);
```
в сервисе производить необходимые проверки и вызов метода репозитория.

```java
  public Invitee save(Invitee invitee) {
    if (repository.findByEmail(invitee.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(invitee.getEmail());
    }
    return  repository.save(invitee);
  }
```

---

## Issue #16: Бизнес-логика в контроллере

**Категория:** Code Quality
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 58, метод delete

**Что плохо:**
```java
    if (invitee != null) {
    repository.delete(invitee);
    }
```

**Почему плохо:**
Нарушение Single Responsibility Principe - контроллер не должен отвечать за работу с репозиторием, проверка дублирования email и прочих бизнес-ограничений это задача сервиса.

**Как исправить:**
В контроллере вызывать удаление сущности через сервис
```java
return service.delete(request);
```
в сервисе производить необходимые проверки и вызов метода репозитория.

```java
  public void delete(UUID id) {
    if (!repository.existsById(id)) {
      throw new EntityNotFoundException("Invitee", id.toString());
    }
    repository.deleteById(id);
  }
```
---

## Issue #17: Бизнес-логика в контроллере

**Категория:** Code Quality
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 72, метод updateStatus

**Что плохо:**
```java
      if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
    invitee.setStatus(status);
      } else {
          throw new RuntimeException("Invalid status");
      }
```

**Почему плохо:**
Нарушение Single Responsibility Principe - контроллер не должен отвечать за работу с репозиторием, проверка дублирования email и прочих бизнес-ограничений это задача сервиса.

**Как исправить:**
В контроллере вызывать изменение статуса сущности через сервис
```java
service.updateStatus(id, status);
```
в сервисе производить необходимые проверки и вызов метода репозитория.

```java
  public void delete(UUID id) {
  if (!repository.existsById(id)) {
    throw new EntityNotFoundException("Invitee", id.toString());
  }
  repository.deleteById(id);
}
```
---