package kr.or.kosa.cmsplusmain.domain.simpconsent.verification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import kr.or.kosa.cmsplusmain.domain.payment.entity.CardPayment;

public interface CardPaymentRepository extends JpaRepository<CardPayment, Long> {
}
