package kr.or.kosa.cmsplusmain.domain.simpconsent.verification.controller;

import kr.or.kosa.cmsplusmain.domain.simpconsent.verification.service.CardVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.or.kosa.cmsplusmain.domain.payment.dto.CardInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/simple-consent/card")
@RequiredArgsConstructor
public class CardVerificationController {

    private final CardVerificationService cardVerificationService;

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyCard(@Valid @RequestBody CardInfo cardInfo) {
        boolean isVerified = cardVerificationService.verifyCard(cardInfo);
        return ResponseEntity.ok(isVerified);
    }
}