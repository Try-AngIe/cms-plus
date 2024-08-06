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
import kr.or.kosa.cmsplusmain.domain.billing.service.BillingService;
import lombok.RequiredArgsConstructor;

@Deprecated
@RestController
@RequestMapping("/api/v1/vendor/billing")
@RequiredArgsConstructor
public class BillingController {

	private final BillingService billingService;

	/**
	 * 청구생성
	 * */
	@PostMapping
	public void createBilling(@VendorId Long vendorId, @RequestBody @Valid BillingCreateReq billingCreateReq) {
		billingService.createBilling(vendorId, billingCreateReq);
	}

	/**
	 * 청구서 발송
	 * */
	@GetMapping("send-invoice/{billingId}")
	public void sendInvoice(@VendorId Long vendorId, @PathVariable Long billingId) {
		billingService.sendInvoice(vendorId, billingId);
	}

	/**
	 * 청구서 발송 취소
	 * */
	@GetMapping("invoice/cancel/{billingId}")
	public void cancelInvoice(@VendorId Long vendorId, @PathVariable Long billingId) {
		billingService.cancelInvoice(vendorId, billingId);
	}

	/**
	 * 청구 실시간 결제
	 * */
	@GetMapping("payment/{billingId}")
	public void payRealtimeBilling(@VendorId Long vendorId, @PathVariable Long billingId) {
		billingService.payRealTimeBilling(vendorId, billingId);
	}

	/**
	 * 청구 결제 취소
	 * */
	@GetMapping("payment/{billingId}/cancel")
	public void cancelPay(@VendorId Long vendorId, @PathVariable Long billingId) {
		billingService.cancelPayBilling(vendorId, billingId);
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
	 * 청구서 조회
	 * */
	@GetMapping("invoice/{billingId}")
	public InvoiceRes getInvoice(@PathVariable Long billingId) {
		return billingService.getInvoice(billingId);
	}

	/**
	 * 청구상품 조회
	 * */
	@GetMapping("products/{billingId}")
	public List<BillingProductRes> getBillingProducts(@VendorId Long vendorId, @PathVariable Long billingId) {
		return billingService.getBillingProducts(vendorId, billingId);
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
}
