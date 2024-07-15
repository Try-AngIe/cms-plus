package kr.or.kosa.cmsplusmain.domain.billing.repository;

import org.springframework.stereotype.Repository;

import kr.or.kosa.cmsplusmain.domain.base.repository.BaseRepository;
import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;

@Repository
public interface BillingRepository extends BaseRepository<Billing, Long> {
}
