package kr.or.kosa.cmsplusmain.domain.simpconsent.simpinfo.controller;

import kr.or.kosa.cmsplusmain.domain.member.dto.MemberDetail;
import kr.or.kosa.cmsplusmain.domain.simpconsent.simpinfo.dto.SimpleConsentDTO;
import kr.or.kosa.cmsplusmain.domain.simpconsent.simpinfo.service.SimpleConsentService;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.VendorUserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/simple-consent")
@RequiredArgsConstructor
public class SimpleConsentController {

    private final SimpleConsentService simpleConsentService;
    private static final Logger logger = LoggerFactory.getLogger(SimpleConsentController.class);

    @PostMapping
    public ResponseEntity<MemberDetail> processSimpleConsent(
            @AuthenticationPrincipal VendorUserDetailsDto userDetails,
            @RequestBody SimpleConsentDTO simpleConsentDTO) {
        try {
            Long vendorId = 1L;
            //String vendorName = userDetails.getName();

            logger.info("Processing simple consent for vendor: (ID: {})", vendorId);
            MemberDetail memberDetail = simpleConsentService.processSimpleConsent(vendorId, simpleConsentDTO);
            logger.info("Simple consent processed successfully for vendor: (ID: {})", vendorId);
            return ResponseEntity.ok(memberDetail);
        } catch (Exception e) {
            logger.error("Error processing simple consent: ", e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}