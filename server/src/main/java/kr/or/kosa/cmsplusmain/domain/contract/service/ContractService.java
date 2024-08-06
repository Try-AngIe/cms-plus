package kr.or.kosa.cmsplusmain.domain.contract.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingSearchReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.repository.BillingCustomRepository;
import kr.or.kosa.cmsplusmain.domain.contract.dto.request.ContractCreateReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.request.ContractProductReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.request.ContractSearchReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.request.ContractUpdateReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.response.ContractDetailRes;
import kr.or.kosa.cmsplusmain.domain.contract.dto.response.ContractListItemRes;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractProduct;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.contract.exception.InvalidContractStatusException;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractCustomRepository;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractProductRepository;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractRepository;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.payment.dto.method.PaymentMethodInfoRes;
import kr.or.kosa.cmsplusmain.domain.payment.dto.type.PaymentTypeInfoRes;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.service.PaymentService;
import kr.or.kosa.cmsplusmain.domain.product.repository.ProductCustomRepository;
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
	private final ProductCustomRepository productCustomRepository;

	private final PaymentService paymentService;
	private final ContractProductRepository contractProductRepository;

	@Transactional
	public Long createContract(Long vendorId, Member member, Payment payment, ContractCreateReq contractCreateReq) {
		// 계약 정보를 DB에 저장한다.
		Contract contract = contractCreateReq.toEntity(vendorId, member, payment);
		contract = contractRepository.save(contract);

		// 상품 ID -> 이름
		List<Long> productIds = contractCreateReq.getContractProducts().stream()
			.mapToLong(ContractProductReq::getProductId)
			.boxed().toList();
		Map<Long, String> productIdToName = productCustomRepository.findAllProductNamesById(productIds);

		List<ContractProduct> contractProducts = contractProductRepository.saveAll(
			contractCreateReq.toProductEntities(contract, productIdToName));
		contractProducts.forEach(contract::addContractProduct);
		contract.calculateContractPriceAndProductCnt();

		// 계약 상품 정보를 DB에 저장한다.
		member.getContracts().add(contract);
		member.calcContractPriceAndCnt();
		System.out.println("ERROR2: " + member.getContractPrice() + ", " + member.getContractCount() + ", "
			+ contract.getContractPrice());

		return contract.getId();
	}

	/**
	 * 계약 목록 조회
	 *
	 * 총 발생 쿼리수: 3회
	 * 내용:
	 * 	계약 조회, 계약상품 목록 조회(+? batch_size=100), 전체 개수 조회
	 * */
	public PageRes<ContractListItemRes> searchContracts(Long vendorId, ContractSearchReq search, PageReq pageReq) {
		// 단일 페이지 결과
		List<ContractListItemRes> content = contractCustomRepository
			.findContractListWithCondition(vendorId, search, pageReq)
			.stream()
			.map(ContractListItemRes::fromEntity)
			.toList();

		// 전체 개수
		int totalContentCount = contractCustomRepository.countAllContracts(vendorId, search);

		return new PageRes<>(totalContentCount, pageReq.getSize(), content);
	}

	/**
	 * 계약의 청구 목록
	 *
	 * 총 발생 쿼리수: 4회
	 * 내용:
	 * 	존재여부 확인, 계약 조회, 계약상품 목록 조회(+? batch_size=100), 전체 개수 조회
	 * */
	public PageRes<BillingListItemRes> getBillingsByContract(Long vendorId, Long contractId, PageReq pageReq) {
		validateContractUser(contractId, vendorId);

		BillingSearchReq search = new BillingSearchReq();
		search.setContractId(contractId);

		List<BillingListItemRes> content =
			billingCustomRepository.findBillingListWithCondition(vendorId, search, pageReq);
		int totalContentCount = billingCustomRepository.countAllBillingsByContract(contractId);

		return new PageRes<>(
			totalContentCount,
			pageReq.getSize(),
			content
		);
	}

	/**
	 * 계약 상세
	 *
	 * 총 발생 쿼리수: 3회
	 * 내용:
	 * 	존재여부 확인, 계약 조회, 계약상품 목록 조회(+? batch_size=100)
	 * */
	public ContractDetailRes getContractDetail(Long vendorId, Long contractId) {
		// 고객의 계약 여부 확인
		validateContractUser(contractId, vendorId);

		// 계약과 멤버, 결제정보 결과 동시 조인(fetch join)
		Contract contract = contractCustomRepository.findContractDetailById(contractId);

		// 결제방식 및 수단에 따른 dto 생성
		Payment payment = contract.getPayment();
		PaymentTypeInfoRes paymentTypeInfoRes = paymentService.getPaymentTypeInfoRes(payment);
		PaymentMethodInfoRes paymentMethodInfoRes = paymentService.getPaymentMethodInfoRes(payment);

		// 계약의 청구 개수 및 총액
		Long[] totalCntAndPrice = billingCustomRepository.findBillingCntAndPriceByContract(contractId);

		return ContractDetailRes.fromEntity(contract,
			paymentTypeInfoRes, paymentMethodInfoRes,
			totalCntAndPrice[0], totalCntAndPrice[1]);
	}

	/**
	 * 계약 수정
	 *
	 * 총 발생 쿼리수: 3회
	 * 내용:
	 * 	존재여부 확인, 계약 조회, 계약상품 목록 조회(+? batch_size=100), 신규 계약상품 생성(*N), 계약이름 수정, 계약상품 삭제(*N)
	 * */
	@Transactional
	public void updateContract(Long vendorId, Long contractId, ContractUpdateReq contractUpdateReq) {
		// 고객의 계약 여부 확인
		validateContractUser(contractId, vendorId);

		Contract contract = contractRepository.findById(contractId).orElseThrow();

		if (contract.getContractStatus() != ContractStatus.ENABLED) {
			throw new InvalidContractStatusException("진행 중인 계약만 수정 가능합니다");
		}

		// 계약 이름 수정
		contract.setContractName(contractUpdateReq.getContractName());

		// 신규 계약상품
		List<ContractProduct> newContractProducts = convertToContractProducts(contractUpdateReq.getContractProducts());

		// 계약상품목록 수정
		updateContractProducts(contract, newContractProducts);
	}

	/**
	 * 계약 상품 요청 -> 계약 상품 엔티티 변환 메서드
	 *
	 * 요청에서 상품의 ID를 받아서
	 * 상품의 ID를 토대로 상품 이름을 가져온다.
	 *
	 * 상품이름을 계약상품 테이블에 저장해
	 * 계약상품 조회시 상품 이름만을 가져오기위한 조인을 없앤다.
	 *
	 * 상품 설정에서 이름 수정 불가 필요
	 * */
	private List<ContractProduct> convertToContractProducts(List<ContractProductReq> contractProductReqs) {
		// 상품 ID
		List<Long> productIds = contractProductReqs.stream()
			.mapToLong(ContractProductReq::getProductId)
			.boxed().toList();

		// 상품 ID -> 이름
		Map<Long, String> productIdToName = productCustomRepository.findAllProductNamesById(productIds);

		// 청구 상품 목록
		return contractProductReqs
			.stream()
			.map(dto -> dto.toEntity(productIdToName.get(dto.getProductId())))
			.toList();
	}

	/**
	 * 기존 청구상품과 신규 청구상품 비교해서
	 * 새롭게 추가되거나 삭제된 것 그리고 수정된것 반영
	 * */
	private void updateContractProducts(Contract contract, List<ContractProduct> mNewContractProducts) {
		Set<ContractProduct> oldContractProducts = contract.getContractProducts();
		List<ContractProduct> newContractProducts = new ArrayList<>(mNewContractProducts);

		// 삭제될 계약상품 ID
		// 삭제 상태 수정을 bulk update 사용해 업데이트 쿼리 횟수 감소 목적
		List<ContractProduct> toRemoves = new ArrayList<>();

		// 수정될 계약상품 수정
		for (ContractProduct oldContractProduct : oldContractProducts) {

			// 수정된 계약상품은 리스트에서 삭제해서 insert 되지 않도록 한다.
			ContractProduct updatedNewContractProduct = null;

			for (ContractProduct newContractProduct : newContractProducts) {
				if (newContractProduct.equals(oldContractProduct)) {
					oldContractProduct.update(newContractProduct.getPrice(), newContractProduct.getQuantity());
					updatedNewContractProduct = newContractProduct;
					break;
				}
			}

			// 신규 상품 목록에 없는 기존 상품은 삭제한다.
			if (updatedNewContractProduct == null) {
				toRemoves.add(oldContractProduct);
			} else {
				newContractProducts.remove(updatedNewContractProduct);
			}
		}

		// 새롭게 추가되는 계약상품 저장
		newContractProducts.forEach(contract::addContractProduct);
		// 삭제된 상품
		toRemoves.forEach(contract::removeContractProduct);

		contract.calculateContractPriceAndProductCnt();
	}

	/**
	 * 계약 ID 존재여부
	 * 계약이 현재 로그인 고객의 계약인지 여부
	 * */
	public void validateContractUser(Long contractId, Long vendorId) {
		if (!contractCustomRepository.isExistContractByUsername(contractId, vendorId)) {
			throw new EntityNotFoundException("계약 ID 없음(" + contractId + ")");
		}
	}
}
