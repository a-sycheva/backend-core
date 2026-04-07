package ru.mentee.power.crm.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

  Optional<Lead> findByEmail(String email);

  List<Lead> findByStatus(LeadStatus status);

  List<Lead> findByCompany(Company company);

  long countByStatus(LeadStatus status);

  boolean existsByEmail(String email);

  // JPQL запросы (объектный язык)
  @Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
  List<Lead> findByStatusIn(@Param("statuses") List<LeadStatus> statuses);

  @Query("SELECT l FROM Lead l WHERE l.createdAt > :date")
  List<Lead> findCreatedAfter(@Param("date") LocalDateTime date);

  @Query("SELECT l FROM Lead l WHERE l.company = :company ORDER BY l.createdAt DESC")
  List<Lead> findByCompanyOrderedByDate(@Param("company") String company);

  // Методы с пагинацией
  Page<Lead> findAll(Pageable pageable);

  Page<Lead> findByStatus(LeadStatus status, Pageable pageable);

  Page<Lead> findByCompany(Company company, Pageable pageable);

  Page<Lead> findByStatusAndCompany(LeadStatus status, Company company, Pageable pageable);

  @Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
  Page<Lead> findByStatusInPaged(@Param("statuses") List<LeadStatus> statuses, Pageable pageable);

  // Bulk операции
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Lead l SET l.status = :newStatus WHERE l.status = :oldStatus")
  int updateStatusBulk(
      @Param("oldStatus") LeadStatus oldStatus,
      @Param("newStatus") LeadStatus newStatus
  );

  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM Lead l WHERE l.status = :status")
  int deleteByStatusBulk(
      @Param("status") LeadStatus status
  );

  //Native-запросы
  @Query(value = "SELECT * FROM leads WHERE email = ?1", nativeQuery = true)
  public Optional<Lead> findByEmailNative(String email);

  @Query(value = "SELECT * FROM leads WHERE status = ?1", nativeQuery = true)
  public List<Lead> findByStatusNative(String status);

  // Pessimistic lock для критических операций (конверсия Lead→Deal)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT l FROM Lead l WHERE l.id = :id")
  Optional<Lead> findByIdForUpdate(@Param("id") UUID id);

  // Pessimistic lock для блокировки по email
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT l FROM Lead l WHERE l.email = :email")
  Optional<Lead> findByEmailForUpdate(@Param("email") String email);
}