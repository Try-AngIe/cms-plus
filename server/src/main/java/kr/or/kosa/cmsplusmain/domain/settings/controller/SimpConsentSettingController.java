package kr.or.kosa.cmsplusmain.domain.settings.controller;

import kr.or.kosa.cmsplusmain.domain.settings.dto.AvailableOptionsDto;
import kr.or.kosa.cmsplusmain.domain.settings.dto.SimpConsentSettingDto;
import kr.or.kosa.cmsplusmain.domain.settings.service.SimpConsentSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vendor/simple-consent")
@RequiredArgsConstructor
public class SimpConsentSettingController {

    private final SimpConsentSettingService simpConsentSettingService;

    /* 고객 간편동의 설정 세팅 조회 */
    @GetMapping
    public ResponseEntity<SimpConsentSettingDto> getSetting() {
        String username  = "vendor1";
        return ResponseEntity.ok(simpConsentSettingService.getSetting(username));
    }

    /* 고객 간편동의 설정 세팅 수정 */
    @PutMapping
    public ResponseEntity<SimpConsentSettingDto> updateSetting(@RequestBody SimpConsentSettingDto dto) {
        String username  = "vendor1";
        return ResponseEntity.ok(simpConsentSettingService.updateSetting(username, dto));
    }

    /* 간편동의 상품, 결제수단 리스트 조회 */
    @GetMapping("/available-options")
    public ResponseEntity<?> getAvailableOptions() {
        String username  = "vendor1";
        AvailableOptionsDto options = simpConsentSettingService.getAvailableOptions(username);
        return ResponseEntity.ok(options);
    }

}