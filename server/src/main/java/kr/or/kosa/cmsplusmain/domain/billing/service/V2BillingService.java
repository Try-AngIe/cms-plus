package kr.or.kosa.cmsplusmain.domain.billing.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingCreateReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingProductReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingSearchReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.request.BillingUpdateReq;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingDetailRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingListItemRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.BillingProductRes;
import kr.or.kosa.cmsplusmain.domain.billing.dto.response.InvoiceRes;
import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingProduct;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingState;
import kr.or.kosa.cmsplusmain.domain.billing.exception.BillingNotFoundException;
import kr.or.kosa.cmsplusmain.domain.billing.repository.V2BillingProductRepository;
import kr.or.kosa.cmsplusmain.domain.billing.repository.V2BillingRepository;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.exception.ContractNotFoundException;
import kr.or.kosa.cmsplusmain.domain.contract.repository.V2ContractRepository;
import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import kr.or.kosa.cmsplusmain.domain.kafka.dto.messaging.EmailMessageDto;
import kr.or.kosa.cmsplusmain.domain.kafka.dto.messaging.SmsMessageDto;
import kr.or.kosa.cmsplusmain.domain.kafka.dto.payment.AccountPaymentDto;
import kr.or.kosa.cmsplusmain.domain.kafka.dto.payment.CardPaymentDto;
import kr.or.kosa.cmsplusmain.domain.kafka.service.KafkaMessagingService;
import kr.or.kosa.cmsplusmain.domain.kafka.service.KafkaPaymentService;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.payment.dto.method.CMSMethodRes;
import kr.or.kosa.cmsplusmain.domain.payment.dto.method.CardMethodRes;
import kr.or.kosa.cmsplusmain.domain.payment.dto.type.PaymentTypeInfoRes;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.service.PaymentService;
import kr.or.kosa.cmsplusmain.util.FormatUtil;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class V2BillingService {
	private final V2BillingRepository billingRepository;
	private final V2BillingProductRepository billingProductRepository;
	private final V2ContractRepository contractRepository;
	private final KafkaMessagingService kafkaMessagingService;
	private final KafkaPaymentService kafkaPaymentService;

	private final PaymentService paymentService;

	@Value("${host.front}")
	private String FRONT_HOST_URL;

	/**
	 * 청구생성 |
	 * 2 + n 쿼리 발생 | 계약 존재여부, 청구 생성, 청구상품 생성 * n
	 * */
	@Transactional
	public void createBilling(Long vendorId, BillingCreateReq billingCreateReq) {
		if (!contractRepository.existsContractByVendorId(vendorId, billingCreateReq.getContractId())) {
			throw new ContractNotFoundException("계약이 없습니다");
		}

		List<BillingProduct> billingProducts = billingCreateReq.getProducts().stream()
			.map(BillingProductReq::toEntity)
			.toList();

		Billing billing = new Billing(
			Contract.of(billingCreateReq.getContractId()),
			billingCreateReq.getBillingType(),
			billingCreateReq.getBillingDate(),
			// 청구 생성시 결제일을 넣어주는데 연월일 형식으로 넣어준다.
			// 정기 청구 시 필요한 약정일은 입력된 결제일에서 일 부분만 빼서 사용
			// ex. 입력 결제일=2024.07.13 => 약정일=13
			billingCreateReq.getBillingDate().getDayOfMonth(),
			billingProducts);
		billingRepository.save(billing);
	}

	/**
	 * 청구목록 조회 |
	 * 2회 쿼리 발생 | 청구목록조회, 전체 개수(페이징)
	 * */
	public PageRes<BillingListItemRes> searchBillings(Long vendorId, BillingSearchReq search, PageReq pageReq) {
		List<BillingListItemRes> content = billingRepository.searchBillings(vendorId, search, pageReq);

		int totalContentCount = billingRepository.countSearchedBillings(vendorId, search).intValue();

		return new PageRes<>(totalContentCount, pageReq.getSize(), content);
	}

	/**
	 * 청구상세 조회 |
	 * 2회 쿼리 발생 | 청구존재여부, 청구상세
	 * */
	public BillingDetailRes getBillingDetail(Long vendorId, Long billingId) {
		validateBillingOwner(billingId, vendorId);
		Billing billing = billingRepository.findBillingWithContract(billingId);

		Map<BillingState.Field, BillingState> fieldToState = Arrays.stream(BillingState.Field.values())
			.collect(Collectors.toMap(field -> field, field -> field.checkState(billing)));

		return BillingDetailRes.fromEntity(billing, fieldToState);
	}

	/**
	 * 회원 청구서 조회
	 * 삭제된 청구서도 조회해, 회원에게 삭제시점을 보여준다.
	 * */
	public InvoiceRes getInvoice(Long billingId) {
		System.out.println("ERROR: invocie start");
		Billing billing = billingRepository.findByIdIncludingDeleted(billingId);

		if (billing == null) {
			throw new BillingNotFoundException("해당하는 청구서가 존재하지 않습니다.");
		}
		if (billing.isDeleted()) {
			throw new BillingNotFoundException(
				"삭제된 청구서입니다. (%s)".formatted(billing.getDeletedDateTime().toString())
			);
		}

		Contract contract = billing.getContract();
		Payment payment = contract.getPayment();
		PaymentTypeInfoRes paymentTypeInfoRes = paymentService.getPaymentTypeInfoRes(payment);

		System.out.println("ERROR: invoice end");

		return InvoiceRes.fromEntity(billing, contract.getMember(), paymentTypeInfoRes);
	}

	/**
	 * 청구상품 조회
	 * 2회 쿼리 발생 | 청구존재여부, 청구상품
	 * */
	public List<BillingProductRes> getBillingProducts(Long vendorId, Long billingId) {
		validateBillingOwner(billingId, vendorId);
		return billingProductRepository.findAllByBillingId(billingId).stream()
			.map(BillingProductRes::fromEntity)
			.toList();
	}

	/**
	 * 청구 수정
	 * 7+a+b회 쿼리 발생 | 청구존재여부, 청구 조회, 청구상품 조회, 청구상품 생성 * a, 청구 수정, 청구상품 수정 * b, 청구상품 삭제
	 * */
	@Transactional
	public void updateBilling(Long vendorId, Long billingId, BillingUpdateReq billingUpdateReq) {
		validateBillingOwner(billingId, vendorId);
		Billing billing = billingRepository.findById(billingId);
		BillingState.Field.UPDATE.validateState(billing);

		billing.updateBillingDate(billingUpdateReq.getBillingDate());
		billing.setInvoiceMessage(billingUpdateReq.getInvoiceMessage());

		List<BillingProduct> newBillingProducts = billingUpdateReq.getBillingProducts().stream()
			.map(BillingProductReq::toEntity)
			.toList();

		updateBillingProducts(billing, newBillingProducts);
	}

	/**
	 * 기존 청구상품과 신규 청구상품 비교해서
	 * 새롭게 추가되거나 삭제된 것 그리고 수정된것 반영
	 * */
	private void updateBillingProducts(Billing billing, List<BillingProduct> mNewBillingProducts) {

		Set<BillingProduct> oldBillingProducts = billing.getBillingProducts();
		List<BillingProduct> newBillingProducts = new ArrayList<>(mNewBillingProducts);

		// 삭제될 청구상품 ID
		// 삭제 상태 수정을 bulk update 사용해 업데이트 쿼리 횟수 감소 목적
		List<BillingProduct> toRemoves = new ArrayList<>();

		// 수정될 청구상품 수정
		for (BillingProduct oldBillingProduct : oldBillingProducts) {

			// 수정된 청구상품은 리스트에서 삭제해서 insert 되지 않도록 한다.
			BillingProduct updatedNewBillingProduct = null;

			for (BillingProduct newBillingProduct : newBillingProducts) {
				if (newBillingProduct.equals(oldBillingProduct)) {
					oldBillingProduct.update(newBillingProduct.getPrice(), newBillingProduct.getQuantity());
					updatedNewBillingProduct = newBillingProduct;
					break;
				}
			}

			// 신규 상품 목록에 없는 기존 상품은 삭제한다.
			if (updatedNewBillingProduct == null) {
				toRemoves.add(oldBillingProduct);
			} else {
				newBillingProducts.remove(updatedNewBillingProduct);
			}
		}

		// 새롭게 추가되는 청구상품 저장
		newBillingProducts.forEach(billing::addBillingProduct);
		// 삭제된 상품
		toRemoves.forEach(billing::removeBillingProduct);

		billing.calculateBillingPriceAndProductCnt();
	}

	/**
	 * 청구 삭제 | 연관된 청구상품도 동시에 삭제된다.
	 * 5회 쿼리 발생 | 청구존재여부, 청구 조회, 청구상품 조회, 청구삭제, 청구상품 삭제
	 * */
	@Transactional
	public void deleteBilling(Long vendorId, Long billingId) {
		validateBillingOwner(billingId, vendorId);
		Billing billing = billingRepository.findById(billingId);
		BillingState.Field.DELETE.validateState(billing);

		List<Long> billingProductIds = billing.getBillingProducts().stream()
			.mapToLong(BillingProduct::getId)
			.boxed().toList();

		// 청구 상품은 bulk update 한 쿼리 내에서 삭제처리
		billing.deleteWithoutProducts();
		billingProductRepository.deleteAllById(billingProductIds);
	}

	/**
	 * 청구서 발송
	 * 3회 쿼리발생 | 존재여부, 청구조회, 청구상태수정
	 * */
	@Transactional
	public void sendInvoice(Long vendorId, Long billingId) {
		validateBillingOwner(billingId, vendorId);
		Billing billing = billingRepository.findById(billingId);
		BillingState.Field.SEND_INVOICE.validateState(billing);

		Contract contract = billing.getContract();
		Member member = contract.getMember();

		String invoiceMessage = createInvoiceMessage(billing, member);
		sendInvoiceMessage(invoiceMessage, member);

		billing.setInvoiceSent();
	}

	/**
	 * 회원이 받을 청구서 링크 문자(이메일) 내용 생성.
	 * */
	private String createInvoiceMessage(Billing billing, Member member) {
		String memberName = member.getName();
		String invoiceName = billing.getInvoiceName();
		String billingPrice = FormatUtil.formatMoney(billing.getBillingPrice());
		String billingDate = billing.getBillingDate().toString();

		String url = FRONT_HOST_URL + "/member/invoice/" + billing.getId();
		return
			"""
				%s님의 청구서가 도착했습니다.
						\t
				- 청구서명: %s
				- 납부할금액: %s원
				- 납부 기한: %s
						\t
				납부하기: %s
			\t""".formatted(memberName, invoiceName, billingPrice, billingDate, url)
				.trim();
	}

	/**
	 * 문자 발송 이벤트 카프카 전송
	 * */
	private void sendInvoiceMessage(String message, Member member) {
		//청구서 링크 발송
		MessageSendMethod sendMethod = member.getInvoiceSendMethod();

		switch (sendMethod) {
			case SMS -> {
				SmsMessageDto smsMessageDto = new SmsMessageDto(message, member.getPhone());
				kafkaMessagingService.produceMessaging(smsMessageDto);
			}
			case EMAIL -> {
				EmailMessageDto emailMessageDto = new EmailMessageDto(message, member.getEmail());
				kafkaMessagingService.produceMessaging(emailMessageDto);
			}
		}
	}

	/**
	 * 청구서 발송 취소
	 * 3회 쿼리 | 존재여부, 청구조회, 청구상태수정
	 * */
	@Transactional
	public void cancelInvoice(Long vendorId, Long billingId) {
		validateBillingOwner(billingId, vendorId);
		Billing billing = billingRepository.findById(billingId);
		BillingState.Field.CANCEL_INVOICE.validateState(billing);

		billing.setInvoiceCancelled();
	}

	/**
	 * 청구 실시간 결제
	 * 3회 쿼리 | 존재여부, 청구조회, 청구상태수정
	 * */
	@Transactional
	public void payRealTimeBilling(Long vendorId, Long billingId) {
		validateBillingOwner(billingId, vendorId);
		Billing billing = billingRepository.findById(billingId);
		BillingState.Field.PAY_REALTIME.validateState(billing);

		sendPayRealtimeEvent(billing);
		billing.setPaid();
	}

	private void sendPayRealtimeEvent(Billing billing) {
		Long billingId = billing.getId();

		// TODO 반정규화 필요성 검토
		Contract contract = billing.getContract();
		Member member = contract.getMember();
		Payment payment = contract.getPayment();
		PaymentMethod method = payment.getPaymentMethod();

		switch (method) {
			case CARD -> {
				CardMethodRes cardMethodRes = (CardMethodRes)paymentService.getPaymentMethodInfoRes(payment);
				CardPaymentDto cardPaymentDto = new CardPaymentDto(billingId, member.getPhone(),
					cardMethodRes.getCardNumber());
				kafkaPaymentService.producePayment(cardPaymentDto);
			}
			case CMS -> {
				CMSMethodRes cmsMethodRes = (CMSMethodRes)paymentService.getPaymentMethodInfoRes(payment);
				AccountPaymentDto accountPaymentDto = new AccountPaymentDto(billingId, member.getPhone(),
					cmsMethodRes.getAccountNumber());
				kafkaPaymentService.producePayment(accountPaymentDto);
			}

		}
	}

	/**
	 * 청구 실시간 결제 취소
	 * 3회 쿼리 | 존재여부, 청구조회, 청구상태수정
	 * */
	@Transactional
	public void cancelPayBilling(Long vendorId, Long billingId) {
		validateBillingOwner(billingId, vendorId);
		Billing billing = billingRepository.findById(billingId);
		BillingState.Field.CANCEL_PAYMENT.validateState(billing);

		billing.setPayCanceled();
	}

	/**
	 * 청구 존재여부
	 * 청구가 현재 로그인 고객의 청구이면서 존재하는 청구 여부 확인
	 * */
	private void validateBillingOwner(Long billingId, Long vendorId) {
		if (!billingRepository.existByVendorId(billingId, vendorId)) {
			throw new BillingNotFoundException("존재하지 않는 청구입니다");
		}
	}
}
