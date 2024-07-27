package kr.or.kosa.cmsplusmain.domain.vendor.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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

import jakarta.servlet.http.Cookie;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEnum;
import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.Identifier.IdFindRes;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.Identifier.SmsIdFindReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.RefreshTokenRes;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.SignupReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.authenticationNumber.NumberReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.password.PwResetReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.password.SmsPwFindReq;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.UserRole;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import kr.or.kosa.cmsplusmain.domain.vendor.jwt.JWTUtil;
import kr.or.kosa.cmsplusmain.domain.vendor.service.VendorService;

@WebMvcTest(VendorController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
class VendorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VendorService vendorService;

	@MockBean
	private JWTUtil jwtUtil;

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
			.alwaysDo(MockMvcResultHandlers.print())
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

	private Vendor createTestVendor() {
		return Vendor.builder()
			.username(generateValidUsername())
			.password(generateValidPassword())
			.name(generateValidName())
			.email(generateValidEmail())
			.phone(generateValidPhone())
			.homePhone(generateValidHomePhone())
			.department("영업부")
			.role(UserRole.ROLE_VENDOR)
			.build();
	}

	private String generateValidUsername() {
		return "user" + random.nextInt(10000);
	}

	private String generateValidPassword() {
		return "Password1!";
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

	@Test
	void joinTest() throws Exception {
		Vendor testVendor = createTestVendor();
		SignupReq signupReq = new SignupReq(testVendor.getUsername(), testVendor.getPassword(), testVendor.getName(),
			testVendor.getEmail(), testVendor.getPhone(), testVendor.getHomePhone(),
			testVendor.getDepartment());

		mockMvc.perform(post("/api/v1/vendor/auth/join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupReq)))
			.andExpect(status().isCreated())
			.andDo(document("vendor-join",
				requestFields(
					fieldWithPath("username").description("사용자 아이디"),
					fieldWithPath("password").description("비밀번호"),
					fieldWithPath("name").description("이름"),
					fieldWithPath("email").description("이메일"),
					fieldWithPath("phone").description("전화번호"),
					fieldWithPath("homePhone").description("집 전화번호").optional(),
					fieldWithPath("department").description("부서")
				)
			));
	}

	@Test
	void isExistUsernameTest() throws Exception {
		Vendor testVendor = createTestVendor();
		given(vendorService.isExistUsername(any())).willReturn(true);

		mockMvc.perform(get("/api/v1/vendor/auth/check-username")
				.param("username", testVendor.getUsername()))
			.andExpect(status().isOk())
			.andDo(document("vendor-check-username",
				queryParameters(
					parameterWithName("username").description("확인할 사용자 아이디")
				),
				responseBody()
			));
	}

	@Test
	void refreshTest() throws Exception {
		RefreshTokenRes refreshTokenRes = RefreshTokenRes.builder()
			.accessToken("new.access.token")
			.build();

		given(vendorService.refresh(any(), any())).willReturn(refreshTokenRes);

		mockMvc.perform(post("/api/v1/vendor/auth/refresh")
				.cookie(new Cookie("refresh_token", "old.refresh.token")))
			.andExpect(status().isOk())
			.andDo(document("vendor-refresh",
				responseFields(
					fieldWithPath("accessToken").description("새로 발급된 액세스 토큰")
				)
			));
	}

	@Test
	void findIdentifierTest() throws Exception {
		Vendor testVendor = createTestVendor();
		IdFindRes idFindRes = new IdFindRes(testVendor.getUsername());
		given(vendorService.findIdentifire(any())).willReturn(idFindRes);

		SmsIdFindReq smsIdFindReq = new SmsIdFindReq("123456", testVendor.getName(), testVendor.getPhone());

		mockMvc.perform(post("/api/v1/vendor/auth/id-inquiry")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(smsIdFindReq)))
			.andExpect(status().isOk())
			.andDo(document("vendor-find-identifier",
				requestFields(
					fieldWithPath("method").description("인증 방법"),
					fieldWithPath("name").description("사용자 이름"),
					fieldWithPath("authenticationNumber").description("인증 번호"),
					fieldWithPath("phone").description("전화번호")
				),
				responseFields(
					fieldWithPath("username").description("찾은 사용자 아이디")
				)
			));
	}

	@Test
	void findPasswordTest() throws Exception {
		Vendor testVendor = createTestVendor();
		given(vendorService.findPassword(any())).willReturn(true);

		SmsPwFindReq smsPwFindReq = new SmsPwFindReq("123456", testVendor.getUsername(), testVendor.getPhone());

		mockMvc.perform(post("/api/v1/vendor/auth/pw-inquiry")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(smsPwFindReq)))
			.andExpect(status().isOk())
			.andDo(document("vendor-find-password",
				requestFields(
					fieldWithPath("method").description("인증 방법"),
					fieldWithPath("username").description("사용자 아이디"),
					fieldWithPath("authenticationNumber").description("인증 번호"),
					fieldWithPath("phone").description("전화번호")
				),
				responseBody()
			));
	}

	@Test
	void resetPasswordTest() throws Exception {
		Vendor testVendor = createTestVendor();
		PwResetReq pwResetReq = new PwResetReq(testVendor.getUsername(), generateValidPassword(), generateValidPassword());

		mockMvc.perform(post("/api/v1/vendor/auth/pw-reset")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pwResetReq)))
			.andExpect(status().isOk())
			.andDo(document("vendor-reset-password",
				requestFields(
					fieldWithPath("username").description("사용자 아이디"),
					fieldWithPath("newPassword").description("새 비밀번호")
				)
			));
	}

	@Test
	void requestAuthenticationNumberTest() throws Exception {
		Vendor testVendor = createTestVendor();
		NumberReq numberReq = new NumberReq();
		numberReq.setUserInfo(testVendor.getName());
		numberReq.setMethodInfo(testVendor.getPhone());
		numberReq.setMethod(MessageSendMethod.SMS);

		mockMvc.perform(post("/api/v1/vendor/auth/request-number")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(numberReq)))
			.andExpect(status().isOk())
			.andDo(document("vendor-request-authentication-number",
				requestFields(
					fieldWithPath("userInfo").description("사용자 정보"),
					fieldWithPath("methodInfo").description("연락처 정보"),
					fieldWithPath("method").description("인증 방법")
				)
			));
	}
}