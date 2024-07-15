package kr.or.kosa.cmsplusmain.domain.simpconsent.verification.service;

import kr.or.kosa.cmsplusmain.domain.payment.validator.CardNumberValidator;
import kr.or.kosa.cmsplusmain.domain.simpconsent.verification.repository.CardPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.kosa.cmsplusmain.domain.payment.dto.CardInfo;
import kr.or.kosa.cmsplusmain.domain.payment.entity.CardPayment;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardVerificationService {

    private final CardNumberValidator cardNumberValidator;

    @Transactional
    public boolean verifyCard(CardInfo cardInfo) {
        log.info("Verifying card: {}", cardInfo.getCardNumber());
        return cardNumberValidator.isValid(cardInfo.getCardNumber(), null);
    }

}
