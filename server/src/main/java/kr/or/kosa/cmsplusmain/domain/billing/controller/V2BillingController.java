package kr.or.kosa.cmsplusmain.domain.billing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.base.security.VendorId;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingCreateReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingDetailRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingSearchReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingUpdateReq;
import kr.or.kosa.cmsplusmain.domain.billing.service.NewBillingService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/vendor/billing")
@RequiredArgsConstructor
public class V2BillingController {

	private final NewBillingService newBillingService;

	/**
	 * 청구생성
	 * */
	@PostMapping
	public void createBilling(@VendorId Long vendorId, @RequestBody @Valid BillingCreateReq billingCreateReq) {
		newBillingService.createBilling(vendorId, billingCreateReq);
	}

	/**
	 * 청구목록 조회
	 * */
	@GetMapping
	public PageRes<BillingListItemRes> searchBillings(@VendorId Long vendorId, BillingSearchReq search, PageReq pageReq) {
		return newBillingService.searchBillings(vendorId, search, pageReq);
	}

	/**
	 * 청구상세 조회
	 * */
	@GetMapping("/{billingId}")
	public BillingDetailRes getBillingDetail(@VendorId Long vendorId, @PathVariable Long billingId) {
		return newBillingService.getBillingDetail(vendorId, billingId);
	}

	/**
	 * 청구 수정
	 * */
	@PutMapping("/{billingId}")
	public void updateBilling(@VendorId Long vendorId, @PathVariable Long billingId, @RequestBody @Valid BillingUpdateReq billingUpdateReq) {
		newBillingService.updateBilling(vendorId, billingId, billingUpdateReq);
	}
}
