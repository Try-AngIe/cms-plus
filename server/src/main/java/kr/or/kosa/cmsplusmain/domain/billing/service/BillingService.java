package kr.or.kosa.cmsplusmain.domain.billing.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
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
import kr.or.kosa.cmsplusmain.domain.billing.repository.BillingCustomRepository;
import kr.or.kosa.cmsplusmain.domain.billing.repository.BillingProductRepository;
import kr.or.kosa.cmsplusmain.domain.billing.repository.BillingRepository;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractCustomRepository;
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
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BillingService {

	// 청구서 URL(청구 ID), 청구서 메시지 내용
	private static final String INVOICE_URL_FORMAT = "https://localhost:8080/invoice/%d";
	private static final String INVOICE_MESSAGE_FORMAT =
		"""
			%s님의 청구서가 도착했습니다.
					\t
			- 청구서명: %s
			- 납부할금액: %s원
			- 납부 기한: %s
					\t
			납부하기: %s
		\t""".trim();
	private final BillingRepository billingRepository;
	private final BillingCustomRepository billingCustomRepository;
	private final BillingProductRepository billingProductRepository;
	private final ContractCustomRepository contractCustomRepository;
	private final KafkaMessagingService kafkaMessagingService;
	private final KafkaPaymentService kafkaPaymentService;
	private final PaymentService paymentService;

	/*
	 * 청구서 발송
	 * */
	@Transactional
	public void sendInvoice(Long vendorId, Long billingId) {
		Billing billing = validateAndGetBilling(vendorId, billingId);
		BillingState.Field.SEND_INVOICE.validateState(billing);

		Contract contract = billing.getContract();
		Member member = contract.getMember();

		String invoiceMessage = createInvoiceMessage(billing, member);
		sendInvoiceMessage(invoiceMessage, member);

		billing.setInvoiceSent();
	}

	/*
	 * 회원이 받을 청구서 링크 문자(이메일) 내용을 만들어준다.
	 * */
	private String createInvoiceMessage(Billing billing, Member member) {
		// 메시지 내용
		String memberName = member.getName();
		String invoiceName = billing.getInvoiceName();
		String billingPrice = FormatUtil.formatMoney(billing.getBillingPrice());
		String billingDate = billing.getBillingDate().toString();
		String url = INVOICE_URL_FORMAT.formatted(billing.getId());

		return INVOICE_MESSAGE_FORMAT
			.formatted(memberName, invoiceName, billingPrice, billingDate, url)
			.trim();
	}

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

	/*
	 * 청구서 발송 취소
	 * */
	@Transactional
	public void cancelInvoice(Long vendorId, Long billingId) {
		Billing billing = validateAndGetBilling(vendorId, billingId);
		BillingState.Field.CANCEL_INVOICE.validateState(billing);

		billing.setInvoiceCancelled();
	}

	/*
	 * 청구 실시간 결제
	 * */
	@Transactional
	public void payRealTimeBilling(Long vendorId, Long billingId) {
		Billing billing = validateAndGetBilling(vendorId, billingId);
		BillingState.Field.PAY_REALTIME.validateState(billing);

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

		billing.setPaid();
	}

	/*
	 * 청구 실시간 결제 취소
	 * */
	@Transactional
	public void cancelPayBilling(Long vendorId, Long billingId) {
		Billing billing = validateAndGetBilling(vendorId, billingId);
		BillingState.Field.CANCEL_PAYMENT.validateState(billing);

		billing.setPayCanceled();
	}

	/**
	 * 청구 생성 |
	 * 계약이 존재하는지 확인 후에 생성한다 |
	 * 2+n회 쿼리 발생 | 존재여부 확인, 청구 생성, 청구상품 생성 * n
	 * */
	@Transactional
	public void createBilling(Long vendorId, BillingCreateReq billingCreateReq) {
		if (!contractCustomRepository.isExistContractByUsername(billingCreateReq.getContractId(), vendorId)) {
			throw new EntityNotFoundException("해당하는 계약이 존재하지 않습니다");
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
	 * 3+x회 쿼리 발생 | 청구목록조회, 청구상품조회 * x(배치사이즈: 100), 전체 개수(페이징)
	 * */
	public PageRes<BillingListItemRes> searchBillings(Long vendorId, BillingSearchReq search, PageReq pageReq) {
		List<BillingListItemRes> content =
			billingCustomRepository.findBillingListWithCondition(vendorId, search, pageReq);

		int totalContentCount = (int)billingCustomRepository.countBillingListWithCondition(vendorId, search);

		return new PageRes<>(totalContentCount, pageReq.getSize(), content);
	}

	/**
	 * 청구상세 조회 |
	 * 2회 쿼리 발생 | 청구존재여부, 청구상세
	 * */
	public BillingDetailRes getBillingDetail(Long vendorId, Long billingId) {
		validateVendorHasBilling(billingId, vendorId);
		Billing billing = billingCustomRepository.findBillingWithContract(billingId);

		Map<BillingState.Field, BillingState> fieldToState = Arrays.stream(BillingState.Field.values())
			.collect(Collectors.toMap(field -> field, field -> field.checkState(billing)));

		return BillingDetailRes.fromEntity(billing, fieldToState);
	}

	/**
	 * 청구서 조회 |
	 * 2회 쿼리 발생 | 청구존재여부, 청구상세
	 * */
	public InvoiceRes getInvoice(Long billingId) {
		Billing billing = billingCustomRepository.findBillingWithContract(billingId);

		Contract contract = billing.getContract();
		Member member = contract.getMember();
		Payment payment = contract.getPayment();
		PaymentTypeInfoRes paymentTypeInfoRes = paymentService.getPaymentTypeInfoRes(payment);

		return InvoiceRes.fromEntity(billing, member, paymentTypeInfoRes);
	}

	/**
	 * 특정 청구의 모든 청구상품 조회
	 * 2회 쿼리 발생 | 청구존재여부, 청구상품
	 * */
	public List<BillingProductRes> getBillingProducts(Long vendorId, Long billingId) {
		validateVendorHasBilling(billingId, vendorId);
		return billingCustomRepository.findAllBillingProducts(billingId).stream()
			.map(BillingProductRes::fromEntity)
			.toList();
	}

	/**
	 * 청구 수정
	 * 6+a+b+x회 쿼리 발생 | 청구존재여부, 청구 조회, 청구상품 조회 * x(배치사이즈: 100), 청구상품 생성 * a, 청구 수정, 청구상품 수정 * b, 청구상품 삭제
	 * */
	@Transactional
	public void updateBilling(Long vendorId, Long billingId, BillingUpdateReq billingUpdateReq) {
		Billing billing = validateAndGetBilling(vendorId, billingId);
		BillingState.Field.UPDATE.validateState(billing);

		billing.updateBillingDate(billingUpdateReq.getBillingDate());
		billing.setInvoiceMessage(billingUpdateReq.getInvoiceMessage());

		List<BillingProduct> newBillingProducts = billingUpdateReq.getBillingProducts().stream()
			.map(BillingProductReq::toEntity)
			.toList();

		updateBillingProducts(billing, newBillingProducts);
	}

	/**
	 * 청구 삭제 | 연관된 청구상품도 동시에 삭제된다.
	 * 5+x회 쿼리 발생 | 청구존재여부, 청구 조회, 청구상품 조회 * x(배치사이즈: 100), 청구삭제, 청구상품 삭제
	 * */
	@Transactional
	public void deleteBilling(Long vendorId, Long billingId) {
		Billing billing = validateAndGetBilling(vendorId, billingId);
		BillingState.Field.DELETE.validateState(billing);

		List<Long> billingProductIds = billing.getBillingProducts().stream()
			.mapToLong(BillingProduct::getId)
			.boxed().toList();

		billing.deleteWithoutProducts();
		billingProductRepository.deleteAllById(billingProductIds);
	}

	/**
	 * 기존 청구상품과 신규 청구상품 비교해서
	 * 새롭게 추가되거나 삭제된 것 그리고 수정된것 반영
	 * */
	private void updateBillingProducts(Billing billing, List<BillingProduct> mNewBillingProducts) {

		Set<BillingProduct> oldBillingProducts = billing.getBillingProducts();
		List<BillingProduct> newBillingProducts = new ArrayList<>(mNewBillingProducts);

		// 삭제될 청구상품 ID
		// 삭제 상태 수정을 IN을 사용해 업데이트 쿼리 횟수 감소 목적
		List<Long> toRemoveIds = new ArrayList<>();

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
				toRemoveIds.add(oldBillingProduct.getId());
			} else {
				newBillingProducts.remove(updatedNewBillingProduct);
			}
		}

		// 새롭게 추가되는 청구상품 저장
		newBillingProducts.forEach(bp -> bp.setBilling(billing));
		billingProductRepository.saveAll(newBillingProducts);

		// 없어진 청구상품 삭제
		billingProductRepository.deleteAllById(toRemoveIds);
	}

	/**
	 * 청구 존재여부
	 * 청구가 현재 로그인 고객의 청구이면서 존재하는 청구 여부 확인
	 * */
	private void validateVendorHasBilling(Long billingId, Long vendorId) {
		if (!billingCustomRepository.isExistBillingByUsername(billingId, vendorId)) {
			throw new BillingNotFoundException("존재하지 않는 청구입니다");
		}
	}

	/**
	 * 청구 존재여부
	 * 청구가 현재 로그인 고객의 청구이면서 존재하는 청구 여부 확인 후 청구 리턴
	 * */
	private Billing validateAndGetBilling(Long vendorId, Long billingId) {
		if (!billingCustomRepository.isExistBillingByUsername(billingId, vendorId)) {
			throw new BillingNotFoundException("존재하지 않는 청구입니다");
		}
		return billingRepository.findById(billingId)
			.orElseThrow(() -> new BillingNotFoundException("청구를 찾을 수 없습니다"));
	}
}
