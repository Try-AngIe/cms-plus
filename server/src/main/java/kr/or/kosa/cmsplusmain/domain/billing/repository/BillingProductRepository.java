package kr.or.kosa.cmsplusmain.domain.billing.repository;

import org.springframework.stereotype.Repository;

import kr.or.kosa.cmsplusmain.domain.base.repository.BaseRepository;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingProduct;

@Repository
public interface BillingProductRepository extends BaseRepository<BillingProduct, Long> {
}
