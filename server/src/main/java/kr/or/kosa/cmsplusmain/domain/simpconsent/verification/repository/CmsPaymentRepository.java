package kr.or.kosa.cmsplusmain.domain.simpconsent.verification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import kr.or.kosa.cmsplusmain.domain.payment.entity.CmsPayment;

public interface CmsPaymentRepository extends JpaRepository<CmsPayment, Long> {
}
