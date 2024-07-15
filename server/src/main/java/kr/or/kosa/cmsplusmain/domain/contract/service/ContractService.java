package kr.or.kosa.cmsplusmain.domain.contract.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.BillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.repository.BillingCustomRepository;
import kr.or.kosa.cmsplusmain.domain.contract.dto.ContractCreateReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.ContractDetailRes;
import kr.or.kosa.cmsplusmain.domain.contract.dto.ContractListItemRes;
import kr.or.kosa.cmsplusmain.domain.contract.dto.ContractProductReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.ContractSearchReq;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractProduct;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractCustomRepository;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractService {

	private final ContractRepository contractRepository;
	private final ContractCustomRepository contractCustomRepository;
	private final BillingCustomRepository billingCustomRepository;

	/*
	 * 계약 목록 조회
	 * */
	public PageRes<ContractListItemRes> searchContracts(String vendorUsername, ContractSearchReq search, PageReq pageReq)
	{
		// 1개 페이지 결과
		List<ContractListItemRes> content = contractCustomRepository
			.findContractListWithCondition(vendorUsername, search, pageReq)
			.stream()
			.map(ContractListItemRes::fromEntity)
			.toList();

		// 전체 개수
		int totalContentCount = contractCustomRepository.countAllContracts(vendorUsername, search);

		return new PageRes<>(totalContentCount, pageReq.getSize(), content);
	}

	/*
	 * 계약의 청구 목록
	 * */
	public PageRes<BillingListItemRes> getBillingsByContract(
		String vendorUsername, Long contractId, PageReq pageReq)
	{
		// 고객의 계약 여부 확인
		validateContractUser(contractId, vendorUsername);

		List<BillingListItemRes> content = billingCustomRepository.findBillingsByContractId(contractId, pageReq)
			.stream()
			.map(BillingListItemRes::fromEntity)
			.toList();

		int totalContentCount = billingCustomRepository.countAllBillingsByContract(contractId);

		return new PageRes<>(
			totalContentCount,
			pageReq.getSize(),
			content
		);
	}

	/*
	 * 계약 상세
	 * */
	public ContractDetailRes getContractDetail(String vendorUsername, Long contractId) {
		// 고객의 계약 여부 확인
		validateContractUser(contractId, vendorUsername);

		Contract contract = contractCustomRepository.findContractDetailById(contractId).orElseThrow();
		return ContractDetailRes.fromEntity(contract);
	}

	/*
	* 계약 이름 및 상품 목록 수정
	* */
	@Transactional
	public void updateContract(String vendorUsername, Long contractId, ContractCreateReq contractCreateReq) {
		// 고객의 계약 여부 확인
		validateContractUser(contractId, vendorUsername);

		// 계약 이름 수정
		Contract contract = contractRepository.findById(contractId).orElseThrow();
		contract.setName(contractCreateReq.getContractName());

		// 신규 계약상품
		List<ContractProduct> newContractProducts = contractCreateReq.getContractProducts()
			.stream()
			.map(ContractProductReq::toEntity)
			.toList();

		// 계약상품 수정
		updateContractProduct(contract, newContractProducts);
	}

	/*
	* 기존 계약상품과 신규 계약상품 비교해서
	* 새롭게 추가되거나 삭제된 것만 수정 반영
	* */
	private void updateContractProduct(Contract contract, List<ContractProduct> newContractProducts) {
		// 기존 계약상품
		List<ContractProduct> oldContractProducts = contract.getContractProducts();

		// 신규 계약상품에만 존재하는 것 추가
		newContractProducts.stream()
			.filter(ncp -> !oldContractProducts.contains(ncp))
			.forEach(contract::addContractProduct);

		// 기존 계약상품에만 존재하는 것 삭제
		oldContractProducts.stream()
			.filter(ocp -> !newContractProducts.contains(ocp))
			.toList()
			.forEach(contract::removeContractProduct);
	}

	/*
	* 계약 ID 존재여부
	* 계약이 현재 로그인 고객의 계약인지 여부
	* */
	private void validateContractUser(Long contractId, String vendorUsername) {
		if (!contractCustomRepository.isExistContractByUsername(contractId, vendorUsername)) {
			throw new EntityNotFoundException("계약 ID 없음(" + contractId + ")");
		}
	}
}
