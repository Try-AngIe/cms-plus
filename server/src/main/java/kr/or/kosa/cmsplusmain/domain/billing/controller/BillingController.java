package kr.or.kosa.cmsplusmain.domain.billing.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
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
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingCreateReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingDetailRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingSearchReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingUpdateReq;
import kr.or.kosa.cmsplusmain.domain.billing.service.BillingService;
import lombok.RequiredArgsConstructor;

// TODO security 연동

@RestController
@RequestMapping("/api/v1/vendor/billing")
@RequiredArgsConstructor
public class BillingController {

	private final BillingService billingService;

	/*
	 * 청구생성
	 * */
	@PostMapping
	public void createBilling(@RequestBody @Valid BillingCreateReq billingCreateReq) {
		String vendorUsername = "vendor1";
		billingService.createBilling(vendorUsername, billingCreateReq);
	}

	/*
	 * 청구목록 조회
	 * */
	@GetMapping
	public PageRes<BillingListItemRes> getBillingListWithCondition(BillingSearchReq search, PageReq pageReq) {
		String vendorUsername = "vendor1";
		return billingService.searchBillings(vendorUsername, search, pageReq);
	}

	/*
	 * 청구상세 조회
	 * */
	@GetMapping("/{billingId}")
	public BillingDetailRes getBillingDetail(@PathVariable Long billingId) {
		String vendorUsername = "vendor1";
		return billingService.getBillingDetail(vendorUsername, billingId);
	}

	/*
	* 청구 수정
	* */
	@PutMapping("/{billingId}")
	public void updateBilling(@PathVariable Long billingId, @RequestBody @Valid BillingUpdateReq billingUpdateReq) {
		String vendorUsername = "vendor1";
		billingService.updateBilling(vendorUsername, billingId, billingUpdateReq);
	}

	/*
	* 청구 삭제
	* */
	@DeleteMapping("/{billingId}")
	public void deleteBilling(@PathVariable Long billingId) {
		String vendorUsername = "vendor1";
		billingService.deleteBilling(vendorUsername, billingId);
	}
}
