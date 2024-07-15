package kr.or.kosa.cmsplusmain.domain.simpconsent.verification.controller;

import kr.or.kosa.cmsplusmain.domain.simpconsent.verification.service.CmsVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.or.kosa.cmsplusmain.domain.payment.dto.CMSInfo;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/simple-consent/cms")
@RequiredArgsConstructor
public class CmsVerificationController {

    private final CmsVerificationService cmsVerificationService;

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyCms(@Valid @RequestBody CMSInfo cmsInfo) {
        boolean isVerified = cmsVerificationService.verifyCms(cmsInfo);
        return ResponseEntity.ok(isVerified);
    }
}
