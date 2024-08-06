package kr.or.kosa.cmsplusmain.domain.billing.repository;

import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBillingProduct.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kr.or.kosa.cmsplusmain.domain.base.repository.V2BaseRepository;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingProduct;

@Repository
public class V2BillingProductRepository extends V2BaseRepository<BillingProduct, Long> {

	@Transactional
	public void deleteAllById(List<Long> ids) {
		update(billingProduct)
			.where(billingProduct.id.in(ids))
			.set(billingProduct.deleted, true)
			.set(billingProduct.deletedDateTime, LocalDateTime.now())
			.execute();
	}

	public List<BillingProduct> findAllByBillingId(Long billingId) {
		return selectFrom(billingProduct)
			.where(billingProduct.billing.id.eq(billingId))
			.fetch();
	}
}
