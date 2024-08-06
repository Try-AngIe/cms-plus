

package kr.or.kosa.cmsplusmain.domain.simpconsent.simpinfo.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.kosa.cmsplusmain.domain.billing.repository.BillingCustomRepository;
import kr.or.kosa.cmsplusmain.domain.contract.dto.ContractDto;
import kr.or.kosa.cmsplusmain.domain.contract.dto.response.ContractProductRes;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.exception.ContractNotFoundException;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractCustomRepository;
import kr.or.kosa.cmsplusmain.domain.contract.service.ContractService;
import kr.or.kosa.cmsplusmain.domain.kafka.dto.messaging.MessageDto;
import kr.or.kosa.cmsplusmain.domain.kafka.dto.messaging.SmsMessageDto;
import kr.or.kosa.cmsplusmain.domain.kafka.service.KafkaMessagingService;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberDetail;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberDto;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.member.exception.MemberNotFoundException;
import kr.or.kosa.cmsplusmain.domain.member.repository.MemberCustomRepository;
import kr.or.kosa.cmsplusmain.domain.member.repository.MemberRepository;
import kr.or.kosa.cmsplusmain.domain.payment.dto.method.PaymentMethodInfoRes;
import kr.or.kosa.cmsplusmain.domain.payment.dto.type.PaymentTypeInfoRes;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.AutoPaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentTypeInfo;
import kr.or.kosa.cmsplusmain.domain.payment.exception.InvalidPaymentTypeException;
import kr.or.kosa.cmsplusmain.domain.payment.service.PaymentService;
import kr.or.kosa.cmsplusmain.domain.product.dto.ProductListItemRes;
import kr.or.kosa.cmsplusmain.domain.product.repository.ProductCustomRepository;
import kr.or.kosa.cmsplusmain.domain.simpconsent.simpinfo.dto.SimpConsentInfoRes;
import kr.or.kosa.cmsplusmain.domain.simpconsent.simpinfo.dto.SimpConsentSignReq;
import kr.or.kosa.cmsplusmain.domain.simpconsent.simpinfo.dto.SimpleConsentContractDTO;
import kr.or.kosa.cmsplusmain.domain.simpconsent.simpinfo.dto.SimpleConsentMemberDTO;
import kr.or.kosa.cmsplusmain.domain.simpconsent.simpinfo.dto.SimpleConsentPaymentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimpleConsentService {

    private final MemberRepository memberRepository;
    private final MemberCustomRepository memberCustomRepository;
    private final BillingCustomRepository billingCustomRepository;
    private final ProductCustomRepository productCustomRepository;
    private final ContractCustomRepository contractCustomRepository;
    private final PaymentService paymentService;
    private final ContractService contractService;
    private final KafkaMessagingService kafkaMessagingService;

    private static final String SIMPCONSENT_MESSAGE_FORMAT =
            """
            %s님의 간편동의 URL이 도착했습니다.
            
            - URL: %s
           
            """.trim();

    @Value("${host.front}")
    private String FRONT_HOST;

    @Transactional
    public MemberDetail processSimpleConsent(Long vendorId, SimpleConsentMemberDTO memberDTO,
                                             SimpleConsentPaymentDTO paymentDTO,
                                             SimpleConsentContractDTO contractDTO) {

        Member member = null;

        if (memberCustomRepository.idExistMemberByPhone(vendorId, memberDTO.getPhone())) {
            member = memberCustomRepository.findMemberByPhone(vendorId, memberDTO.getPhone()).orElseThrow();
        } else {
            member = memberRepository.save(memberDTO.toMemberEntity(vendorId));
        }

        Payment payment = paymentService.createPayment(paymentDTO.toPaymentCreateReq());

        Map<Long, String> productIdToNameMap = productCustomRepository.findAllProductNamesById(
                contractDTO.getItems().stream().map(ProductListItemRes::getProductId).collect(Collectors.toList())
        );

        contractService.createContract(vendorId, member, payment, contractDTO.toContractCreateReq());

//        int billingCount = billingCustomRepository.findBillingStandardByMemberId(vendorId, member.getId());
//        Long totalBillingPrice = billingCustomRepository.findBillingProductByMemberId(vendorId, member.getId());

        return MemberDetail.fromEntity(member, 0, 0L);
    }

    public void sendReqUrl(Long vendorId, Long contractId) {
        if (!contractCustomRepository.isExistContractByUsername(contractId, vendorId)) {
            throw new MemberNotFoundException("본인의 계약에만 전송 가능합니다.");
        }

        Contract contract = contractCustomRepository.findContractDetailById(contractId);
        Member member = contract.getMember();
        Payment payment = contract.getPayment();

        if (!payment.canReqSimpConsent() ) {
            throw new InvalidPaymentTypeException(
                "[%s]는 간편동의가 불가능합니다".formatted(payment.getPaymentType().getTitle()));
        }

        if (contract.getContractStatus() == ContractStatus.DISABLED) {
            throw new InvalidPaymentTypeException(
                "종료된 계약은 간편동의가 불가능합니다");
        }

        String url = "%s/member/simpconsent?contract=%d&vendor=%d".formatted(
            FRONT_HOST, contractId, vendorId
        );
        log.info("{} 간편동의 요청 링크가 발송됨", url);

        String text = SIMPCONSENT_MESSAGE_FORMAT.formatted(member.getName(), url).trim();
        String phone = member.getPhone();

        MessageDto messageDto = new SmsMessageDto(text, phone);

        kafkaMessagingService.produceMessaging(messageDto);
    }

    public SimpConsentInfoRes getSimpleConsentInfo(Long vendorId, Long contractId) {
        if (!contractCustomRepository.isExistContractByUsername(contractId, vendorId)) {
            throw new ContractNotFoundException("해당하는 계약이 없습니다");
        }

        Contract contract = contractCustomRepository.findContractDetailById(contractId);
        Member member = contract.getMember();
        Payment payment = contract.getPayment();

        if (!payment.canReqSimpConsent()) {
            throw new InvalidPaymentTypeException("간편동의가 불가능한 결제방식입니다.");
        }

        ContractDto contractDto = ContractDto.fromEntity(contract);
        List<ContractProductRes> contractProductRes = contract.getContractProducts().stream()
            .map(ContractProductRes::fromEntity)
            .toList();
        MemberDto memberDto = MemberDto.fromEntity(member);
        PaymentTypeInfoRes paymentTypeInfoRes = paymentService.getPaymentTypeInfoRes(payment);
        PaymentMethodInfoRes paymentMethodInfoRes = paymentService.getPaymentMethodInfoRes(payment);

        return SimpConsentInfoRes.builder()
            .contract(contractDto)
            .contractProducts(contractProductRes)
            .member(memberDto)
            .paymentTypeInfo(paymentTypeInfoRes)
            .paymentMethodInfo(paymentMethodInfoRes)
            .build();
    }

    @Transactional
    public void setSigned(Long vendorId, SimpConsentSignReq simpConsentSignReq) {
        Long contractId = simpConsentSignReq.getContractId();

        if (!contractCustomRepository.isExistContractByUsername(contractId, vendorId)) {
            throw new ContractNotFoundException("해당하는 계약이 없습니다");
        }

        Contract contract = contractCustomRepository.findContractDetailById(contractId);
        Payment payment = contract.getPayment();

        if (!payment.canReqSimpConsent()) {
            throw new InvalidPaymentTypeException("간편동의가 불가능한 결제방식입니다.");
        }

        PaymentTypeInfo paymentTypeInfo = payment.getPaymentTypeInfo();
        // proxy 객체인 경우 자식 클래스로 캐스팅이 안된다.
        // 프록시를 자식 객체로써 받아온다.
        if (!Hibernate.isInitialized(paymentTypeInfo)) {
            LazyInitializer lazyInitializer = HibernateProxy.extractLazyInitializer(paymentTypeInfo);
            if (lazyInitializer != null) {
                paymentTypeInfo = (PaymentTypeInfo) lazyInitializer.getImplementation();
            } else {
                throw new IllegalStateException("Provided payment type is not initialized");
            }
        }

        AutoPaymentType autoPaymentType = (AutoPaymentType)paymentTypeInfo;
        autoPaymentType.setSignImgUrl(simpConsentSignReq.getSignImgUrl());
    }
}
