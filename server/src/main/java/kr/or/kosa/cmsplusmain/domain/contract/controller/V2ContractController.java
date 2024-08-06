package kr.or.kosa.cmsplusmain.domain.contract.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.base.security.VendorId;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.contract.dto.request.ContractSearchReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.response.ContractProductRes;
import kr.or.kosa.cmsplusmain.domain.contract.dto.response.V2ContractListItemRes;
import kr.or.kosa.cmsplusmain.domain.contract.service.V2ContractService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/vendor/contract")
@RequiredArgsConstructor
public class V2ContractController {

	private final V2ContractService contractService;

	/**
	 * 계약 목록 조회
	 * */
	@GetMapping
	public PageRes<V2ContractListItemRes> searchContracts(@VendorId Long vendorId, ContractSearchReq contractSearchReq,
		PageReq pageReq) {
		return contractService.searchContracts(vendorId, contractSearchReq, pageReq);
	}

	/**
	 * 계약 목록 조회
	 * */
	@GetMapping("/product/{contractId}")
	public List<ContractProductRes> getContractProducts(@VendorId Long vendorId, @PathVariable Long contractId) {
		return contractService.getContractProducts(vendorId, contractId);
	}

	/**
	 * 계약 상세 조회 - 청구 목록
	 * */
	@GetMapping("/{contractId}/billing")
	public PageRes<BillingListItemRes> getBillingListByContract(@VendorId Long vendorId, @PathVariable Long contractId,
		PageReq pageReq) {
		return contractService.getBillingsByContract(vendorId, contractId, pageReq);
	}

	/**
	 * 계약 상세 조회 - 계약 상품 목록
	 * */
	@GetMapping("/{contractId}/product")
	public List<ContractProductRes> getBillingProductsByContract(@VendorId Long vendorId,
		@PathVariable Long contractId) {
		return contractService.getContractProducts(vendorId, contractId);
	}
}
