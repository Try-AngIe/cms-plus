package kr.or.kosa.cmsplusmain.domain.billing.controller;

import java.util.List;

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
import kr.or.kosa.cmsplusmain.domain.base.security.VendorId;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingCreateReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingSearchReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingUpdateReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingDetailRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingProductRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.InvoiceRes;
import kr.or.kosa.cmsplusmain.domain.billing.service.V2BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v2/vendor/billing")
@RequiredArgsConstructor
@Slf4j
public class V2BillingController {

	private final V2BillingService billingService;

	/**
	 * 청구생성
	 * */
	@PostMapping
	public void createBilling(@VendorId Long vendorId, @RequestBody @Valid BillingCreateReq billingCreateReq) {
		billingService.createBilling(vendorId, billingCreateReq);
	}

	/**
	 * 청구목록 조회
	 * */
	@GetMapping
	public PageRes<BillingListItemRes> searchBillings(@VendorId Long vendorId, BillingSearchReq search,
		PageReq pageReq) {
		return billingService.searchBillings(vendorId, search, pageReq);
	}

	/**
	 * 청구상세 조회
	 * */
	@GetMapping("/{billingId}")
	public BillingDetailRes getBillingDetail(@VendorId Long vendorId, @PathVariable Long billingId) {
		return billingService.getBillingDetail(vendorId, billingId);
	}

	/**
	 * 청구상품 조회
	 * */
	@GetMapping("/{billingId}/product")
	public List<BillingProductRes> getBillingProducts(@VendorId Long vendorId, @PathVariable Long billingId) {
		return billingService.getBillingProducts(vendorId, billingId);
	}

	/**
	 * 청구서 조회
	 * */
	@GetMapping("/invoice/{billingId}")
	public InvoiceRes getInvoice(@PathVariable Long billingId) {
		log.info("invoice " + billingId);
		return billingService.getInvoice(billingId);
	}

	/**
	 * 청구 수정
	 * */
	@PutMapping("/{billingId}")
	public void updateBilling(@VendorId Long vendorId, @PathVariable Long billingId,
		@RequestBody @Valid BillingUpdateReq billingUpdateReq) {
		billingService.updateBilling(vendorId, billingId, billingUpdateReq);
	}

	/**
	 * 청구 삭제
	 * */
	@DeleteMapping("/{billingId}")
	public void deleteBilling(@VendorId Long vendorId, @PathVariable Long billingId) {
		billingService.deleteBilling(vendorId, billingId);
	}

	/**
	 * 청구서 발송
	 * */
	@GetMapping("/{billingId}/sendInvoice")
	public void sendInvoice(@VendorId Long vendorId, @PathVariable Long billingId) {
		billingService.sendInvoice(vendorId, billingId);
	}

	/**
	 * 청구서 발송 취소
	 * */
	@GetMapping("/{billingId}/cancelInvoice")
	public void cancelInvoice(@VendorId Long vendorId, @PathVariable Long billingId) {
		billingService.cancelInvoice(vendorId, billingId);
	}

	/**
	 * 청구 실시간 결제
	 * */
	@GetMapping("/{billingId}/realtimePay")
	public void payRealtimeBilling(@VendorId Long vendorId, @PathVariable Long billingId) {
		billingService.payRealTimeBilling(vendorId, billingId);
	}

	/**
	 * 청구 결제 취소
	 * 실시간 결제 외에도 일반 납부자결제의 경우에도 사용되는 api
	 * */
	@GetMapping("/{billingId}/cancelPay")
	public void cancelPay(@VendorId Long vendorId, @PathVariable Long billingId) {
		billingService.cancelPayBilling(vendorId, billingId);
	}
}
