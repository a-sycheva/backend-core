# Руководство по выбору стратегии блокировок

## Когда использовать Pessimistic Locking

### ✅ Подходит для:


- **Критические финансовые операции**: списание денег, обновление баланса
- **Конверсия Lead→Deal**: изменение статуса обеих entity, создание связей
- **Резервирование ограниченного ресурса**: последний билет, товар со счётчиком
- **Операции с высокой конкуренцией**: десятки пользователей одновременно изменяют данные
- **Гарантия отсутствия конфликтов**: бизнес не допускает retry логику


### ❌ НЕ использовать для:

- Обычных CRUD операций с низкой конкуренцией
- Длительных операций (пользователь редактирует форму 5 минут)
- Read-heavy сценариев где запись редка

### Пример использования:


```java
@Transactional
public Deal convertLeadToDeal(UUID leadId) {
  // Блокируем Lead эксклюзивно
  Lead lead =leadRepository.findByIdForUpdate(leadId).orElseThrow();
  lead.setStatus("CONVERTED");
  Deal deal = new Deal(lead);

  leadRepository.save(lead);
  return dealRepository.save(deal);
}
```

## Когда использовать Optimistic Locking

### ✅ Подходит для
- Редактирование профиля: один пользователь редактирует свои данные
- Обновление описания Lead: маловероятно что два менеджера одновременно редактируют
- Изменение настроек: низкая конкуренция, редкие обновления
- Read-mostly данные: справочники, каталоги
- Длительные операции: пользователь может редактировать форму долго

### ❌ НЕ использовать для
- Критических операций где retry неприемлем
- Операций с высокой вероятностью конфликтов
- Когда нет возможности обработать OptimisticLockException

### Пример использования

```Java
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Data
public class Lead {
@Version
@Setter(AccessLevel.NONE) // JPA управляет версией — НЕ создаём setter
private Long version;
// При каждом UPDATE version инкрементируется автоматически
}

@Transactional
public Lead updateLead(UUID id, String newStatus) {
Lead lead = leadRepository.findById(id).orElseThrow();
lead.setStatus(newStatus);
return leadRepository.save(lead);
// Если version конфликтует → OptimisticLockException
}
```

### Обработка OptimisticLockException

#### Вариант 1: Показать пользователю актуальные данные

```Java
@RestController
public class LeadController {

@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
public ResponseEntity<ErrorResponse> handleOptimisticLock(
ObjectOptimisticLockingFailureException e) {

    return ResponseEntity
      .status(HttpStatus.CONFLICT)
      .body(new ErrorResponse(
        "Данные изменены другим пользователем. Пожалуйста обновите страницу."
      ));
}
}
```

#### Вариант 2: Автоматический retry для batch операций
```Java
@Retryable(
include = OptimisticLockException.class,
maxAttempts = 3,
backoff = @Backoff(delay = 100)
)
@Transactional
public void batchUpdateLeads(List<UUID> ids, String newStatus) {
// Автоматически повторится до 3 раз при конфликте
}
```
## Предотвращение Deadlock

Правило: всегда блокировать в одном порядке
```Java
// ❌ ПЛОХО: разный порядок блокировок
void processLeads(UUID id1, UUID id2) {
leadRepository.findByIdForUpdate(id1);
leadRepository.findByIdForUpdate(id2);
}
// ✅ ХОРОШО: сортируем ID перед блокировкой
void processLeads(UUID id1, UUID id2) {
List<UUID> sortedIds = Stream.of(id1, id2)
.sorted()
.toList();

for (UUID id : sortedIds) {
leadRepository.findByIdForUpdate(id);
}
}
```

## Итоговые рекомендации

| Критерий | Pessimistic | Optimistic |
|----------|-------------|------------|
| Вероятность конфликтов | Высокая | Низкая |
| Критичность данных | Высокая | Средняя |
| Latency | Выше (ожидание блокировок) | Ниже |
| Throughput | Ниже | Выше |
| Обработка конфликтов | Автоматическая (ожидание) | Ручная (catch exception) |
| Deadlock риск | Есть | Нет |
| По умолчанию | НЕТ | ДА (`@Version` везде) |

