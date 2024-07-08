package kr.or.kosa.cmsplusmain.domain.base.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

	@CreatedDate
	@Column(name = "created_datetime")
	private LocalDateTime createdDateTime;

	@LastModifiedDate
	@Column(name = "updated_datetime")
	private LocalDateTime updatedDateTime;

	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;

	@Column(name = "deleted_datetime")
	private LocalDateTime deletedDateTime;

	public void delete() {
		deleted = true;
		deletedDateTime = LocalDateTime.now();
	}
}