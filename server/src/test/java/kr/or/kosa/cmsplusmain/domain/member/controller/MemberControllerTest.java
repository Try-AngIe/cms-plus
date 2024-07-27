package kr.or.kosa.cmsplusmain.domain.member.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEnum;
import kr.or.kosa.cmsplusmain.domain.contract.dto.ContractCreateReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.ContractProductReq;
import kr.or.kosa.cmsplusmain.domain.contract.dto.MemberContractListItemRes;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberBillingUpdateReq;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberCreateReq;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberDetail;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberListItemRes;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberSearchReq;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberUpdateReq;
import kr.or.kosa.cmsplusmain.domain.member.entity.Address;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.member.service.MemberService;
import kr.or.kosa.cmsplusmain.domain.payment.dto.PaymentCreateReq;
import kr.or.kosa.cmsplusmain.domain.payment.dto.method.CardMethodReq;
import kr.or.kosa.cmsplusmain.domain.payment.dto.type.AutoTypeReq;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.service.PaymentService;

@WebMvcTest(MemberController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

	@MockBean
	private PaymentService paymentService;

	private ObjectMapper objectMapper;

	private final Random random = new Random();

	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext,
		RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(documentationConfiguration(restDocumentation)
				.operationPreprocessors()
				.withRequestDefaults(prettyPrint())
				.withResponseDefaults(prettyPrint())
			)
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
		this.objectMapper.registerModule(new Jdk8Module());
		this.objectMapper.registerModule(new ParameterNamesModule());
		this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		SimpleModule baseEnumModule = new SimpleModule();
		baseEnumModule.addSerializer(BaseEnum.class, new JsonSerializer<BaseEnum>() {
			@Override
			public void serialize(BaseEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				gen.writeString(value.getCode());
			}
		});
		this.objectMapper.registerModule(baseEnumModule);
	}

	private String generateValidName() {
		return "홍길동" + random.nextInt(100);
	}

	private String generateValidEmail() {
		return "user" + random.nextInt(10000) + "@example.com";
	}

	private String generateValidPhone() {
		return String.format("010%04d%04d", random.nextInt(10000), random.nextInt(10000));
	}

	private String generateValidHomePhone() {
		return String.format("02%04d%04d", random.nextInt(10000), random.nextInt(10000));
	}

	private Member createTestMember() {
		return Member.builder()
			.name(generateValidName())
			.email(generateValidEmail())
			.phone(generateValidPhone())
			.homePhone(generateValidHomePhone())
			.address(new Address("12345", "서울시 강남구", "테헤란로 123"))
			.memo("테스트 학생 메모")
			.enrollDate(LocalDate.now().minusDays(random.nextInt(365)))
			.invoiceSendMethod(MessageSendMethod.EMAIL)
			.autoInvoiceSend(true)
			.autoBilling(true)
			.build();
	}

	@Test
	void getMemberListTest() throws Exception {
		MemberSearchReq searchReq = new MemberSearchReq();
		PageReq pageReq = new PageReq();
		List<MemberListItemRes> memberList = new ArrayList<>();
		memberList.add(MemberListItemRes.fromEntity(createTestMember()));
		PageRes<MemberListItemRes> pageRes = new PageRes<>(1, 10, memberList);

		given(memberService.searchMembers(any(), any(), any())).willReturn(pageRes);

		mockMvc.perform(get("/api/v1/vendor/management/members")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("member-list",
				responseFields(
					fieldWithPath("totalCount").description("총 회원 수"),
					fieldWithPath("totalPage").description("총 페이지 수"),
					fieldWithPath("content").description("회원 목록"),
					fieldWithPath("content[].memberId").description("회원 ID"),
					fieldWithPath("content[].memberName").description("회원 이름"),
					fieldWithPath("content[].memberPhone").description("회원 전화번호"),
					fieldWithPath("content[].memberEmail").description("회원 이메일"),
					fieldWithPath("content[].memberEnrollDate").description("회원 등록일"),
					fieldWithPath("content[].contractPrice").description("계약 금액"),
					fieldWithPath("content[].contractCount").description("계약 수")
				)
			));
	}

	@Test
	void getMemberDetailTest() throws Exception {
		Member testMember = createTestMember();
		MemberDetail memberDetail = MemberDetail.fromEntity(testMember, 5, 1000000L);

		given(memberService.findMemberDetailById(any(), any())).willReturn(memberDetail);

		mockMvc.perform(get("/api/v1/vendor/management/members/{memberId}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("member-detail",
				pathParameters(
					parameterWithName("memberId").description("회원 ID")
				),
				responseFields(
					fieldWithPath("memberId").description("회원 ID"),
					fieldWithPath("memberName").description("회원 이름"),
					fieldWithPath("memberPhone").description("회원 전화번호"),
					fieldWithPath("memberEmail").description("회원 이메일"),
					fieldWithPath("memberHomePhone").description("회원 집 전화번호"),
					fieldWithPath("memberAddress").description("회원 주소"),
					fieldWithPath("memberAddress.zipcode").description("우편번호"),
					fieldWithPath("memberAddress.address").description("주소"),
					fieldWithPath("memberAddress.addressDetail").description("상세주소"),
					fieldWithPath("memberMemo").description("회원 메모"),
					fieldWithPath("memberEnrollDate").description("회원 등록일"),
					fieldWithPath("createdDateTime").description("생성일시"),
					fieldWithPath("modifiedDateTime").description("수정일시"),
					fieldWithPath("contractCount").description("계약 수"),
					fieldWithPath("billingCount").description("청구서 수"),
					fieldWithPath("totalBillingPrice").description("총 청구 금액")
				)
			));
	}

	@Test
	void getMemberContractListTest() throws Exception {
		PageReq pageReq = new PageReq();
		List<MemberContractListItemRes> contractList = new ArrayList<>();
		contractList.add(MemberContractListItemRes.builder()
			.contractId(1L)
			.contractName("테스트계약")
			.contractStartDate(LocalDate.now())
			.contractEndDate(LocalDate.now().plusMonths(12))
			.contractDay(15)
			.contractProducts(Collections.emptyList())
			.contractPrice(1000000L)
			.paymentType(PaymentType.AUTO)
			.contractStatus(ContractStatus.ENABLED)
			.build());
		PageRes<MemberContractListItemRes> pageRes = new PageRes<>(1, 10, contractList);

		given(memberService.findContractListItemByMemberId(any(), any(), any())).willReturn(pageRes);

		mockMvc.perform(get("/api/v1/vendor/management/members/contracts/{memberId}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("member-contract-list",
				pathParameters(
					parameterWithName("memberId").description("회원 ID")
				),
				responseFields(
					fieldWithPath("totalCount").description("총 계약 수"),
					fieldWithPath("totalPage").description("총 페이지 수"),
					fieldWithPath("content").description("계약 목록"),
					fieldWithPath("content[].contractId").description("계약 ID"),
					fieldWithPath("content[].contractName").description("계약명"),
					fieldWithPath("content[].contractStartDate").description("계약 시작일"),
					fieldWithPath("content[].contractEndDate").description("계약 종료일"),
					fieldWithPath("content[].contractDay").description("계약일"),
					fieldWithPath("content[].contractProducts").description("계약 상품 목록"),
					fieldWithPath("content[].contractPrice").description("계약 금액"),
					fieldWithPath("content[].paymentType.code").description("결제 방식 코드"),
					fieldWithPath("content[].paymentType.title").description("결제 방식 제목"),
					fieldWithPath("content[].contractStatus.title").description("계약 상태 제목"),
					fieldWithPath("content[].contractStatus.code").description("계약 상태 코드")
				)
			));
	}

	@Test
	void createMemberTest() throws Exception {
		MemberCreateReq memberCreateReq = new MemberCreateReq();
		memberCreateReq.setMemberName(generateValidName());
		memberCreateReq.setMemberPhone(generateValidPhone());
		memberCreateReq.setMemberEnrollDate(LocalDate.now());
		memberCreateReq.setMemberHomePhone(generateValidHomePhone());
		memberCreateReq.setMemberEmail(generateValidEmail());
		memberCreateReq.setMemberAddress(new Address("12345", "서울시 강남구", "테헤란로 123"));
		memberCreateReq.setMemberMemo("테스트 회원 메모");
		memberCreateReq.setInvoiceSendMethod(MessageSendMethod.EMAIL);
		memberCreateReq.setAutoInvoiceSend(true);
		memberCreateReq.setAutoBilling(true);

		PaymentCreateReq paymentCreateReq = new PaymentCreateReq();
		AutoTypeReq autoTypeReq = AutoTypeReq.builder()
			.consentImgUrl("https://example.com/consent.jpg")
			.signImgUrl("https://example.com/sign.jpg")
			.simpleConsentReqDateTime(LocalDateTime.now())
			.build();
		paymentCreateReq.setPaymentTypeInfoReq(autoTypeReq);

		CardMethodReq cardMethodReq = CardMethodReq.builder()
			.cardNumber("1234-5678-9012-3456")
			.cardMonth(12)
			.cardYear(2025)
			.cardOwner("홍길동")
			.cardOwnerBirth(LocalDate.of(1990, 1, 1))
			.build();
		paymentCreateReq.setPaymentMethodInfoReq(cardMethodReq);

		memberCreateReq.setPaymentCreateReq(paymentCreateReq);

		ContractCreateReq contractCreateReq = new ContractCreateReq();
		contractCreateReq.setContractName("테스트계약");
		contractCreateReq.setContractStartDate(LocalDate.now());
		contractCreateReq.setContractEndDate(LocalDate.now().plusMonths(12));
		contractCreateReq.setContractDay(15);

		ContractProductReq contractProductReq = ContractProductReq.builder()
			.productId(1L)
			.price(10000)
			.quantity(2)
			.build();
		contractCreateReq.setContractProducts(Collections.singletonList(contractProductReq));

		memberCreateReq.setContractCreateReq(contractCreateReq);

		mockMvc.perform(post("/api/v1/vendor/management/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberCreateReq)))
			.andExpect(status().isOk())
			.andDo(document("member-create",
				requestFields(
					fieldWithPath("memberName").description("회원 이름"),
					fieldWithPath("memberPhone").description("회원 전화번호"),
					fieldWithPath("memberEnrollDate").description("회원 등록일"),
					fieldWithPath("memberHomePhone").description("회원 집 전화번호").optional(),
					fieldWithPath("memberEmail").description("회원 이메일"),
					fieldWithPath("memberAddress").description("회원 주소"),
					fieldWithPath("memberAddress.zipcode").description("우편번호"),
					fieldWithPath("memberAddress.address").description("주소"),
					fieldWithPath("memberAddress.addressDetail").description("상세주소"),
					fieldWithPath("memberMemo").description("회원 메모").optional(),
					fieldWithPath("invoiceSendMethod").description("청구서 발송 방법"),
					fieldWithPath("autoInvoiceSend").description("자동 청구서 발송 여부"),
					fieldWithPath("autoBilling").description("자동 청구 여부"),
					fieldWithPath("paymentCreateReq").description("결제 정보"),
					fieldWithPath("paymentCreateReq.paymentTypeInfoReq.paymentType").description("결제 유형 정보"),
					fieldWithPath("paymentCreateReq.paymentTypeInfoReq.consentImgUrl").description("동의 이미지 URL"),
					fieldWithPath("paymentCreateReq.paymentTypeInfoReq.signImgUrl").description("서명 이미지 URL"),
					fieldWithPath("paymentCreateReq.paymentTypeInfoReq.simpleConsentReqDateTime").description("간편 동의 요청 일시"),
					fieldWithPath("paymentCreateReq.paymentMethodInfoReq.paymentMethod").description("결제 방법 정보"),
					fieldWithPath("paymentCreateReq.paymentMethodInfoReq.cardNumber").description("카드 번호"),
					fieldWithPath("paymentCreateReq.paymentMethodInfoReq.cardMonth").description("카드 유효 월"),
					fieldWithPath("paymentCreateReq.paymentMethodInfoReq.cardYear").description("카드 유효 년"),
					fieldWithPath("paymentCreateReq.paymentMethodInfoReq.cardOwner").description("카드 소유자"),
					fieldWithPath("paymentCreateReq.paymentMethodInfoReq.cardOwnerBirth").description("카드 소유자 생년월일"),
					fieldWithPath("contractCreateReq").description("계약 정보"),
					fieldWithPath("contractCreateReq.contractName").description("계약명"),
					fieldWithPath("contractCreateReq.contractStartDate").description("계약 시작일"),
					fieldWithPath("contractCreateReq.contractEndDate").description("계약 종료일"),
					fieldWithPath("contractCreateReq.contractDay").description("계약일"),
					fieldWithPath("contractCreateReq.contractProducts").description("계약 상품 목록"),
					fieldWithPath("contractCreateReq.contractProducts[].productId").description("상품 ID"),
					fieldWithPath("contractCreateReq.contractProducts[].price").description("상품 가격"),
					fieldWithPath("contractCreateReq.contractProducts[].quantity").description("상품 수량")
				)
			));
	}

	@Test
	void updateMemberTest() throws Exception {
		MemberUpdateReq memberUpdateReq = new MemberUpdateReq();
		memberUpdateReq.setMemberName(generateValidName());
		memberUpdateReq.setMemberPhone(generateValidPhone());
		memberUpdateReq.setMemberEnrollDate(LocalDate.now());
		memberUpdateReq.setMemberHomePhone(generateValidHomePhone());
		memberUpdateReq.setMemberEmail(generateValidEmail());
		memberUpdateReq.setMemberAddress(new Address("12345", "서울시 강남구", "테헤란로 123"));
		memberUpdateReq.setMemberMemo("수정된 회원 메모");

		mockMvc.perform(put("/api/v1/vendor/management/members/{memberId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberUpdateReq)))
			.andExpect(status().isOk())
			.andDo(document("member-update",
				pathParameters(
					parameterWithName("memberId").description("회원 ID")
				),
				requestFields(
					fieldWithPath("memberName").description("회원 이름"),
					fieldWithPath("memberPhone").description("회원 전화번호"),
					fieldWithPath("memberEnrollDate").description("회원 등록일"),
					fieldWithPath("memberHomePhone").description("회원 집 전화번호").optional(),
					fieldWithPath("memberEmail").description("회원 이메일"),
					fieldWithPath("memberAddress").description("회원 주소"),
					fieldWithPath("memberAddress.zipcode").description("우편번호"),
					fieldWithPath("memberAddress.address").description("주소"),
					fieldWithPath("memberAddress.addressDetail").description("상세주소"),
					fieldWithPath("memberMemo").description("회원 메모").optional()
				)
			));
	}

	@Test
	void updateMemberBillingTest() throws Exception {
		MemberBillingUpdateReq memberBillingUpdateReq = new MemberBillingUpdateReq();
		memberBillingUpdateReq.setInvoiceSendMethod(MessageSendMethod.SMS);
		memberBillingUpdateReq.setAutoInvoiceSend(false);
		memberBillingUpdateReq.setAutoBilling(true);

		mockMvc.perform(put("/api/v1/vendor/management/members/billing/{memberId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberBillingUpdateReq)))
			.andExpect(status().isOk())
			.andDo(document("member-billing-update",
				pathParameters(
					parameterWithName("memberId").description("회원 ID")
				),
				requestFields(
					fieldWithPath("invoiceSendMethod").description("청구서 발송 방법"),
					fieldWithPath("autoInvoiceSend").description("자동 청구서 발송 여부"),
					fieldWithPath("autoBilling").description("자동 청구 여부")
				)
			));
	}

	@Test
	void deleteMemberTest() throws Exception {
		mockMvc.perform(delete("/api/v1/vendor/management/members/{memberId}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("member-delete",
				pathParameters(
					parameterWithName("memberId").description("회원 ID")
				)
			));
	}

	@Test
	void getInProgressBillingCountTest() throws Exception {
		given(memberService.countAllInProgressBillingByMember(any(), any())).willReturn(5);

		mockMvc.perform(get("/api/v1/vendor/management/members/{memberId}/billing", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("member-in-progress-billing-count",
				pathParameters(
					parameterWithName("memberId").description("회원 ID")
				),
				responseBody()
			));
	}
}