package kr.or.kosa.cmsplusbatch.domain.base.repository;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import kr.or.kosa.cmsplusbatch.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusbatch.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusbatch.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import jakarta.annotation.Nonnull;
import org.springframework.data.repository.query.Param;


/**
 * @deprecated 대신 NewBaseRepository 사용
 * */
@Deprecated
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends Serializable> extends JpaRepository<T, ID> {

	@Override
	@Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
	@Nonnull List<T> findAll();

	@Override
	@Query("SELECT e FROM #{#entityName} e WHERE e.id IN ?1 AND e.deleted = false")
	@Nonnull List<T> findAllById(@Nonnull Iterable<ID> ids);

	@Override
	@Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1 AND e.deleted = false")
	@Nonnull Optional<T> findById(@Nonnull ID id);

	@Override
	@Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = false")
	long count();

	@Override
	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = ?1 AND e.deleted = false")
	boolean existsById(@Nonnull ID id);

	@Override
	@Modifying
	@Query("UPDATE #{#entityName} e SET e.deleted = true WHERE e.id = ?1")
	void deleteById(@Nonnull ID id);

	@Override
	@Modifying
	@Query("UPDATE #{#entityName} e SET e.deleted = true WHERE e.id IN ?1")
	void deleteAllById(@Nonnull Iterable<? extends ID> ids);

	@Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true")
	List<T> findAllDeleted();

	@Modifying
	@Query("UPDATE #{#entityName} e SET e.deleted = false WHERE e.id = ?1")
	void restore(ID id);

}