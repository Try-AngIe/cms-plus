package kr.or.kosa.cmsplusmain.domain.dashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.kosa.cmsplusmain.domain.base.security.VendorId;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.MonthBillingInfoRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.StatInfoRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.dto.TopInfoRes;
import kr.or.kosa.cmsplusmain.domain.dashboard.service.StatService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stat")
@RequiredArgsConstructor
public class StatController {

	private final StatService statService;

	@GetMapping("/info")
	public StatInfoRes getStatInfo(@VendorId Long vendorId, int year, int month) {
		return statService.getStatInfo(vendorId, year, month);
	}

	@GetMapping("/billings")
	public MonthBillingInfoRes getBillingStat(@VendorId Long vendorId, int year, int month) {
		return statService.getMonthBillingInfo(vendorId, year, month);
	}

	@GetMapping("/top")
	public TopInfoRes getTopFive(@VendorId Long vendorId) {
		return statService.getTopInfo(vendorId);
	}
}
