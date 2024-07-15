package kr.or.kosa.cmsplusmain.domain.simpconsent.verification.service;

import kr.or.kosa.cmsplusmain.domain.payment.validator.AccountNumberValidator;
import kr.or.kosa.cmsplusmain.domain.simpconsent.verification.repository.CmsPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.kosa.cmsplusmain.domain.payment.dto.CMSInfo;
import kr.or.kosa.cmsplusmain.domain.payment.entity.CmsPayment;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class CmsVerificationService {

    private final AccountNumberValidator accountNumberValidator;

    @Transactional
    public boolean verifyCms(CMSInfo cmsInfo) {
        // 여기서 실제 CMS 계좌 인증 로직을 구현
        // 외부 은행 API를 호출하거나 내부 로직으로 검증

        log.info("Verifying CMS account: {}", cmsInfo.getAccountNumber());
        boolean isValid = accountNumberValidator.isValid(cmsInfo.getAccountNumber(), null);

        if (isValid) {
            log.info("CMS account verified successfully");
        } else {
            log.warn("CMS account verification failed");
        }

        return isValid;
    }
}
