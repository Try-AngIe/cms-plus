package kr.or.kosa.cmsplusmain.domain.contract.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingSearchReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.repository.V2BillingRepository;
import kr.or.kosa.cmsplusmain.domain.contract.dto.request.ContractSearchReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.response.ContractProductRes;
import kr.or.kosa.cmsplusmain.domain.contract.dto.response.V2ContractListItemRes;
import kr.or.kosa.cmsplusmain.domain.contract.exception.ContractNotFoundException;
import kr.or.kosa.cmsplusmain.domain.contract.repository.V2ContractProductRepository;
import kr.or.kosa.cmsplusmain.domain.contract.repository.V2ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class V2ContractService {

	private final V2ContractRepository contractRepository;
	private final V2BillingRepository billingRepository;
	private final V2ContractProductRepository contractProductRepository;

	/**
	 * 계약 목록 조회 |
	 * 2회 쿼리 발생 | 청구목록조회, 전체 개수(페이징)
	 * */
	public PageRes<V2ContractListItemRes> searchContracts(Long vendorId, ContractSearchReq search, PageReq pageReq) {
		List<V2ContractListItemRes> content = contractRepository.searchContracts(vendorId, search, pageReq);
		long totalContentCount = contractRepository.countSearchedContracts(vendorId, search);
		return new PageRes<>((int)totalContentCount, pageReq.getSize(), content);
	}

	/**
	 * 계약의 모든 청구 목록 조회
	 * 3회 쿼리 발생 | 존재 여부, 청구 목록 조회, 전체 개수(페이징)
	 * */
	public PageRes<BillingListItemRes> getBillingsByContract(Long vendorId, Long contractId, PageReq pageReq) {
		validateContractByVendor(vendorId, contractId);

		BillingSearchReq searchReq = BillingSearchReq.builder()
			.contractId(contractId)
			.build();

		List<BillingListItemRes> content = billingRepository.searchBillings(vendorId, searchReq, pageReq);
		long totalContentCount = billingRepository.countSearchedBillings(vendorId, searchReq);

		return new PageRes<>((int)totalContentCount, pageReq.getSize(), content);
	}

	/**
	 * 계약의 모든 계약상품 |
	 * 2회 쿼리 발생 | 계약 존재여부, 계약 상품조회
	 * */
	public List<ContractProductRes> getContractProducts(Long vendorId, Long contractId) {
		validateContractByVendor(vendorId, contractId);
		return contractProductRepository.findAllByContractId(contractId).stream()
			.map(ContractProductRes::fromEntity)
			.toList();
	}

	private void validateContractByVendor(Long vendorId, Long contractId) {
		if (!contractRepository.existsContractByVendorId(vendorId, contractId)) {
			throw new ContractNotFoundException("계약이 없습니다");
		}
	}
}
