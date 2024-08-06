package kr.or.kosa.cmsplusmain.domain.payment.service;

import jakarta.persistence.EntityNotFoundException;
import kr.or.kosa.cmsplusmain.domain.payment.dto.PaymentBankRes;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Bank;
import kr.or.kosa.cmsplusmain.util.HibernateUtils;
import kr.or.kosa.cmsplusmain.util.RandomNumberGenerator;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.exception.ContractNotFoundException;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractCustomRepository;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractRepository;
import kr.or.kosa.cmsplusmain.domain.payment.dto.PaymentCreateReq;
import kr.or.kosa.cmsplusmain.domain.payment.dto.PaymentUpdateReq;
import kr.or.kosa.cmsplusmain.domain.payment.dto.method.*;
import kr.or.kosa.cmsplusmain.domain.payment.dto.type.*;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.*;
import kr.or.kosa.cmsplusmain.domain.payment.repository.*;
import kr.or.kosa.cmsplusmain.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.stereotype.Service;

import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.CardPaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.CmsPaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethodInfo;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final VirtualAccountPaymentTypeRepository virtualAccountPaymentTypeRepository;
	private final BuyerPaymentTypeRepository buyerPaymentTypeRepository;
	private final AutoPaymentTypeRepository autoPaymentTypeRepository;
	private final CardPaymentMethodRepository cardPaymentMethodRepository;
	private final CmsPaymentMethodRepository cmsPaymentMethodRepository;
	private final PaymentRepository paymentRepository;
	private final ContractRepository contractRepository;
	private final ContractCustomRepository contractCustomRepository;
	private final PaymentCustomRepository paymentCustomRepository;

	public PaymentTypeInfoRes getPaymentTypeInfoRes(Payment payment) {
		System.out.println(payment.getPaymentTypeInfo());
		PaymentTypeInfo paymentTypeInfo = HibernateUtils.getImplements(payment.getPaymentTypeInfo(), PaymentTypeInfo.class);
		if (paymentTypeInfo == null) {
			throw new IllegalStateException("결제방식은 무조건 존재");
			// return null;
		}

		PaymentType paymentType = paymentTypeInfo.getPaymentType();
		return switch (paymentType) {
			case VIRTUAL -> {
				VirtualAccountPaymentType virtualAccountPaymentType = (VirtualAccountPaymentType) paymentTypeInfo;
				yield VirtualAccountTypeRes.builder()
					.bank(virtualAccountPaymentType.getBank())
					.accountNumber(virtualAccountPaymentType.getAccountNumber())
					.accountOwner(virtualAccountPaymentType.getAccountOwner())
					.build();
			}
			case BUYER -> {
				BuyerPaymentType buyerPaymentType = (BuyerPaymentType) paymentTypeInfo;
				yield BuyerTypeRes.builder()
					.availableMethods(buyerPaymentType.getAvailableMethods())
					.build();
			}
			case AUTO -> {
				AutoPaymentType autoPaymentType = (AutoPaymentType) paymentTypeInfo;
				yield AutoTypeRes.builder()
					.consentImgUrl(autoPaymentType.getConsentImgUrl())
					.signImgUrl(autoPaymentType.getSignImgUrl())
					.simpleConsentReqDateTime(autoPaymentType.getSimpleConsentReqDateTime())
					.consentStatus(autoPaymentType.getConsentStatus())
					.build();
			}
		};
	}



	public PaymentMethodInfoRes getPaymentMethodInfoRes(Payment payment) {
		PaymentMethodInfo paymentMethodInfo = HibernateUtils.getImplements(payment.getPaymentMethodInfo(), PaymentMethodInfo.class);

		// 결제수단은 자동결제방식의 경우에만 존재한다.
		if (paymentMethodInfo == null) {
			return null;
		}

		PaymentMethod paymentMethod = paymentMethodInfo.getPaymentMethod();
		return switch (paymentMethod) {
			case CARD -> {
				CardPaymentMethod cardPaymentMethod = (CardPaymentMethod) paymentMethodInfo;
				yield CardMethodRes.builder()
					.cardNumber(FormatUtil.formatCardNumber(cardPaymentMethod.getCardNumber()))
					.cardOwner(cardPaymentMethod.getCardOwner())
					.cardOwnerBirth(cardPaymentMethod.getCardOwnerBirth())
					.cardMonth(cardPaymentMethod.getCardMonth())
					.cardYear(cardPaymentMethod.getCardYear())
					.build();
			}
			case CMS -> {
				CmsPaymentMethod cmsPaymentMethod = (CmsPaymentMethod) paymentMethodInfo;
				yield CMSMethodRes.builder()
					.bank(cmsPaymentMethod.getBank())
					.accountOwner(cmsPaymentMethod.getAccountOwner())
					.accountNumber(FormatUtil.formatAccountNumber(cmsPaymentMethod.getAccountNumber()))
					.accountOwnerBirth(cmsPaymentMethod.getAccountOwnerBirth())
					.build();

			}
			case ACCOUNT -> null;
		};
	}

	/*
	 * 회원 등록 - 결제 정보
	 * */
	@Transactional
	public Payment createPayment(PaymentCreateReq paymentCreateReq) {

		// 결제 방식 정보 ( 자동결제, 납부자결제, 가상계좌 )
		PaymentTypeInfoReq paymentTypeInfoReq = paymentCreateReq.getPaymentTypeInfoReq();
		PaymentTypeInfo paymentTypeInfo = createPaymentTypeInfo(paymentTypeInfoReq);

		// 결제 방식 정보가 자동결제인 경우 : 결제 수단을 등록한다.
		PaymentMethodInfo paymentMethodInfo = null;
		PaymentMethodInfoReq paymentMethodInfoReq = paymentCreateReq.getPaymentMethodInfoReq();
		if(paymentMethodInfoReq != null) {
			// 결제 수단 정보 ( 카드, 실시간CMS )
			paymentMethodInfo = createPaymentMethodInfo(paymentMethodInfoReq);
		}

		// 결제 정보
		Payment payment = Payment.builder()
				.paymentType(paymentTypeInfoReq.getPaymentType())   										  // 결제방식
				.paymentTypeInfo(paymentTypeInfo)  															  // 결제방식 정보
				.paymentMethod(paymentMethodInfoReq != null ? paymentMethodInfoReq.getPaymentMethod() : null) // 결제수단
				.paymentMethodInfo(paymentMethodInfo)														  // 결제수단 정보
				.build();

		return paymentRepository.save(payment);
	}

	/*
	 * 회원 수정 - 결제 정보
	 * */
	// TODO
	// 결제 수정 로직을 변경예정

 	@Transactional
	public void updatePayment(Long vendorId, Long contractId,  PaymentUpdateReq paymentUpdateReq){

		//TODO
		//contractService를 호출 하는 순간 "순환 의존성" 문제가 생긴다 왜 샌기는걸지 알아보자.
		// 고객의 계약 여부 확인
		if (!contractCustomRepository.isExistContractByUsername(contractId, vendorId)) {
			throw new ContractNotFoundException("계약이 존재하지 않습니다");
		}
		Contract contract = contractRepository.findById(contractId).orElseThrow(IllegalArgumentException::new);

		contract.setContractDay(paymentUpdateReq.getContractDay());

		// 고객의 결제 여부 확인
		Long paymentId = contract.getPayment().getId();
		validatePaymentUser(paymentId);
		Payment payment = paymentRepository.findById(paymentId).orElseThrow(IllegalArgumentException::new);


		// 원래 결제 테이블의 결제 타입 삭제
		// 1. paymentTypeInfo 삭제
		payment.getPaymentTypeInfo().delete();

		// 2. paymentType이 AUTO라면, paymentMethodInfo 삭제
		if(payment.getPaymentType().equals(PaymentType.AUTO) ){
			payment.getPaymentMethodInfo().delete();
		}

		// 요청으로 들어온 데이터 확인
		PaymentTypeInfoReq paymentTypeInfoReq = paymentUpdateReq.getPaymentTypeInfoReq();
		PaymentTypeInfo paymentTypeInfo = createPaymentTypeInfo(paymentTypeInfoReq);

		// 결제 방식 정보가 자동결제인 경우 : 결제 수단을 등록한다.
		PaymentMethodInfo paymentMethodInfo = null;
		PaymentMethodInfoReq paymentMethodInfoReq = paymentUpdateReq.getPaymentMethodInfoReq();
		if(paymentMethodInfoReq != null) {
			// 결제 수단 정보 ( 카드, 실시간CMS )
			paymentMethodInfo = createPaymentMethodInfo(paymentMethodInfoReq);
		}

		// 결제 정보 업데이트
		payment.update(paymentTypeInfoReq.getPaymentType(),paymentTypeInfo,
				paymentMethodInfoReq != null ?  paymentMethodInfoReq.getPaymentMethod() : null,
				paymentMethodInfo);

		paymentRepository.save(payment);
	}

	/*
	 * 은행 정보 불러오기
	 * */
	public List<PaymentBankRes> getAllBanks() {
		List<PaymentBankRes> bankList = new ArrayList<>();
		for (Bank bank : Bank.values()) {
			bankList.add(new PaymentBankRes(bank.getCode(), bank.getTitle(), bank.name()));
		}
		return bankList;
	}

	/*
	 * 결제 방식 등록 ( 자동결제, 납부자결제, 가상계좌 )
	 * */
	private PaymentTypeInfo createPaymentTypeInfo(PaymentTypeInfoReq paymentTypeInfoReq) {
		PaymentTypeInfo paymentTypeInfo = null;

		// 결제방식 - 자동결제
		if(paymentTypeInfoReq instanceof AutoTypeReq autoTypeReq){
			AutoPaymentType autoPaymentType = autoTypeReq.toEntity();
			paymentTypeInfo = autoPaymentTypeRepository.save(autoPaymentType);
		}

		// 결제방식 - 납부자결제
		else if(paymentTypeInfoReq instanceof BuyerTypeReq buyerTypeReq){
			BuyerPaymentType buyerPaymentType = buyerTypeReq.toEntity();
			paymentTypeInfo = buyerPaymentTypeRepository.save(buyerPaymentType);
		}

		// 결제방식 - 가상계좌
		else if(paymentTypeInfoReq instanceof VirtualAccountTypeReq virtualAccountTypeReq){

			// 가상계좌 14자리 랜덤으로 생성
			String accountNumber = RandomNumberGenerator.generateRandomNumber(14, PaymentType.VIRTUAL);

			// 가상계좌 DB에 저장
			VirtualAccountPaymentType virtualAccountPaymentType =  virtualAccountTypeReq.toEntity(accountNumber);
			paymentTypeInfo = virtualAccountPaymentTypeRepository.save(virtualAccountPaymentType);
		}

		return paymentTypeInfo;
	}

	/*
	 * 결제 수단 정보 ( 카드, 실시간 CMS )
	 * */
	private PaymentMethodInfo createPaymentMethodInfo(PaymentMethodInfoReq paymentMethodInfoReq) {
		PaymentMethodInfo paymentMethodInfo = null;

		// 결제수단 - Card 결제
		if(paymentMethodInfoReq instanceof CardMethodReq cardMethodReq){
			CardPaymentMethod cardPaymentMethod = cardMethodReq.toEntity();
			paymentMethodInfo = cardPaymentMethodRepository.save(cardPaymentMethod);
		}

		// 결제수단 - 실시간 CMS 결제
		else if(paymentMethodInfoReq instanceof CMSMethodReq cmsMethodReq){
			CmsPaymentMethod cmsPaymentMethod = cmsMethodReq.toEntity();
			paymentMethodInfo = cmsPaymentMethodRepository.save(cmsPaymentMethod);
		}

		return paymentMethodInfo;
	}

	/*
	 * 결제 ID 존재여부
	 * */
	private void validatePaymentUser(Long paymentId) {
		if(!paymentCustomRepository.isExistPaymentId(paymentId)) {
			throw new EntityNotFoundException("결제 ID 없음("+paymentId + ")");
		}
	}


}
