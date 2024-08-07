package kr.or.kosa.cmsplusmain.domain.vendor.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import kr.or.kosa.cmsplusmain.domain.kafka.dto.messaging.EmailMessageDto;
import kr.or.kosa.cmsplusmain.domain.kafka.dto.messaging.MessageDto;
import kr.or.kosa.cmsplusmain.domain.kafka.dto.messaging.SmsMessageDto;
import kr.or.kosa.cmsplusmain.domain.kafka.service.KafkaMessagingService;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.repository.ProductRepository;
import kr.or.kosa.cmsplusmain.domain.settings.entity.SimpConsentSetting;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.Identifier.EmailIdFindReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.Identifier.IdFindReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.Identifier.IdFindRes;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.Identifier.SmsIdFindReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.RefreshTokenRes;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.SignupReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.authenticationNumber.NumberReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.password.EmailPwFindReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.password.PwFindReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.password.PwResetReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.password.SmsPwFindReq;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.UserRole;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import kr.or.kosa.cmsplusmain.domain.vendor.exception.VendorEmailDuplicationException;
import kr.or.kosa.cmsplusmain.domain.vendor.exception.VendorPhoneDuplicationException;
import kr.or.kosa.cmsplusmain.domain.vendor.exception.VendorUsernameDuplicationException;
import kr.or.kosa.cmsplusmain.domain.vendor.jwt.JWTUtil;
import kr.or.kosa.cmsplusmain.domain.vendor.repository.VendorCustomRepository;
import kr.or.kosa.cmsplusmain.domain.vendor.repository.VendorRepository;
import kr.or.kosa.cmsplusmain.util.RandomNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VendorService {
	private final VendorRepository vendorRepository;
	private final VendorCustomRepository vendorCustomRepository;
	private final ProductRepository productRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JWTUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;
 	private final KafkaMessagingService kafkaMessagingService;

	@Transactional
	public void join(SignupReq signupReq) {
		String username = signupReq.getUsername();
		String phone = signupReq.getPhone();
		String email = signupReq.getEmail();
		String password = bCryptPasswordEncoder.encode(signupReq.getPassword());
		UserRole role = UserRole.ROLE_VENDOR;

		// 중복된 아이디가 입력된 경우 예외처리
		boolean isExistUsername = vendorCustomRepository.isExistUsername(username);
		if (isExistUsername) {
			throw new VendorUsernameDuplicationException("아이디 중복되었습니다");
		}

		// 중복된 핸드폰번호가 입력된 경우 예외처리
		boolean isExistPhone = vendorCustomRepository.isExistPhone(phone);
		if (isExistPhone) {
			throw new VendorPhoneDuplicationException("휴대폰 번호가 중복되었습니다");
		}

		// 중복된 이메일이 입력된 경우 예외처리
		boolean isExistEmail = vendorCustomRepository.isExistEmail(email);
		if (isExistEmail) {
			throw new VendorEmailDuplicationException("이메일이 중복되었습니다");
		}

		Vendor vendor = signupReq.toEntity(username, password, role);

		// 간편동의 설정 초기화
		SimpConsentSetting simpConsentSetting = createDefaultSimpConsentSetting();
		vendor.setSimpConsentSetting(simpConsentSetting);

		Vendor mVendor = vendorRepository.save(vendor);

		// 상품 하나 추가
		Product sampleProduct = productRepository.save(Product.builder()
			.name("기본상품")
			.vendor(mVendor)
			.price(0)
			.build());

		mVendor.getSimpConsentSetting().addProduct(sampleProduct);
	}

	// 간편동의 설정 기본값
	private SimpConsentSetting createDefaultSimpConsentSetting() {
		SimpConsentSetting setting = new SimpConsentSetting();

		// 전체 자동결제 수단 추가
		Set<PaymentMethod> autoPaymentMethods = new HashSet<>(PaymentType.getAutoPaymentMethods());
		for (PaymentMethod paymentMethod : autoPaymentMethods) {
			setting.addPaymentMethod(paymentMethod);
		}

		return setting;
	}

	@Transactional
	public RefreshTokenRes refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String refreshToken = null;
		Cookie[] cookies = request.getCookies();

		// Cookie에서 refresh token 접근
		for(Cookie cookie : cookies) {
			System.out.println("cookies == "+ cookie.getName() + " : " + cookie.getValue());
			if (cookie.getName().equals("refresh_token")) {
				refreshToken = cookie.getValue();
			}
		}

		// refreshToken cookie 검증
		if (refreshToken == null) {
			log.info("refreshToken cookie 검증 : refresh token null");
			throw new IllegalArgumentException("refresh token null");
		}

		// 토큰 만료 여부 확인
		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			log.info("refreshToken 기간 만료");
			throw new IllegalArgumentException("Invaild refresh token");
		}

		String category = jwtUtil.getCategory(refreshToken);

		// 토큰이 refresh인지 확인
		if (!category.equals("refresh")) {
			log.info("refreshToken이 아님");
			throw new IllegalArgumentException("Invalid refresh token");
		}

		// 토큰이 Redis에 저장되어 있는지 확인
		String storedRefreshToken = redisTemplate.opsForValue().get(jwtUtil.getUsername(refreshToken));
		if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
			log.info("refreshToken이 래디스에 없음");
			throw new IllegalArgumentException("Invalid refresh token");
		}

		String username = jwtUtil.getUsername(refreshToken);
		String role = jwtUtil.getRole(refreshToken);
		Long id = Long.valueOf(jwtUtil.getId(refreshToken));

		// JWT 토큰 생성
		String newAccessToken = jwtUtil.createJwt("access", username, id, role, 60 * 60 * 1000L);
		String newRefreshToken = jwtUtil.createJwt("refresh", username, id, role, 7 * 24 * 60 * 60 * 1000L);

		// 기존 토큰을 Redis에서 제거 후 새로운 토큰 저장
		redisTemplate.delete(username);
		redisTemplate.opsForValue().set(username, newRefreshToken, 7, TimeUnit.DAYS);

		// refresh token 응답
		response.addCookie(createCookie("refresh_token", newRefreshToken));

		// Json 응답 본문 작성
		return RefreshTokenRes.builder()
			.accessToken(newAccessToken)
			.build();
	}


	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(7 * 24 * 60 * 60);
		//cookie.setSecure(true);
		//cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}

	/*
	 * 아이디 찾기
	 * */
	public IdFindRes findIdentifire(IdFindReq idFindReq) {
		String key = null;
		Vendor vendor = null;
		IdFindRes idFindRes = null;

		// SMS 요청
		if (idFindReq.getMethod() == MessageSendMethod.SMS) {
			SmsIdFindReq smsIdFindReq = (SmsIdFindReq) idFindReq;
			key = smsIdFindReq.getName() +":"+smsIdFindReq.getPhone();

			// 인증번호 일치여부 검증
			String value = redisTemplate.opsForValue().get(key);
			if(value != null && value.equals(smsIdFindReq.getAuthenticationNumber())){
				vendor = vendorCustomRepository.findByNameAndPhone(smsIdFindReq.getName(), smsIdFindReq.getPhone());
			}
		}

		// EMAIL 요청
		else if (idFindReq.getMethod() == MessageSendMethod.EMAIL) {
			EmailIdFindReq emailIdFindReq = (EmailIdFindReq) idFindReq;
			key = emailIdFindReq.getName()+":"+emailIdFindReq.getEmail();

			// 인증번호 일치여부 검증
			String value = redisTemplate.opsForValue().get(key);
			if(value != null && value.equals(emailIdFindReq.getAuthenticationNumber())){
				vendor = vendorCustomRepository.findByNameAndEmail(emailIdFindReq.getName(), emailIdFindReq.getEmail());
			}
		}
		redisTemplate.delete(key);
		if(vendor != null) {
			idFindRes = new IdFindRes(vendor.getUsername());
		}
		return idFindRes;
	}

	/*
	 * 비밀번호 찾기
	 * */
	public boolean findPassword(PwFindReq pwFindReq) {
		String key = null;
		Vendor vendor = null;

		// SMS 요청
		if (pwFindReq.getMethod() == MessageSendMethod.SMS) {
			SmsPwFindReq smsPwFindReq = (SmsPwFindReq) pwFindReq;
			key = smsPwFindReq.getUsername() +":"+smsPwFindReq.getPhone();

			// 인증번호 일치여부 검증
			String value = redisTemplate.opsForValue().get(key);
			if(value != null && value.equals(smsPwFindReq.getAuthenticationNumber())){
				vendor = vendorCustomRepository.findByUsername(smsPwFindReq.getUsername());
			}
		}

		// EMAIL 요청
		else if (pwFindReq.getMethod() == MessageSendMethod.EMAIL) {
			EmailPwFindReq emailPwFindReq = (EmailPwFindReq) pwFindReq;
			key = emailPwFindReq.getUsername()+":"+emailPwFindReq.getEmail();

			// 인증번호 일치여부 검증
			String value = redisTemplate.opsForValue().get(key);
			if(value != null && value.equals(emailPwFindReq.getAuthenticationNumber())){
				vendor = vendorCustomRepository.findByUsername(emailPwFindReq.getUsername());
			}
		}
		redisTemplate.delete(key);
		if(vendor != null) {
			return true;
		}
		return false;
	}

	/*
	 * 비밀번호 재설정
	 * */
	@Transactional
	public void resetPassword(PwResetReq pwResetReq) {

		// 고객 여부 확인
		validateVendorUser(pwResetReq.getUsername());
		Vendor vendor = vendorCustomRepository.findByUsername(pwResetReq.getUsername());
		vendor.setPassword(bCryptPasswordEncoder.encode(pwResetReq.getNewPassword()));
	}

	/*
	 * 인증번호 요청
	 * */
	public void requestAuthenticationNumber(NumberReq numberReq) {

		// 인증번호 생성 (6자리)
		String authenticationNumber = RandomNumberGenerator.generateRandomNumber(6);

		// 메세지 문구 생성
		String messageText = String.format("안녕하세요 효성CMS#입니다. 인증번호[%s]를 입력해 주세요.", authenticationNumber);

		// Redis에 저장 (5분 유효)
		String key = numberReq.getUserInfo() +":"+ numberReq.getMethodInfo();
        redisTemplate.opsForValue().set(key, authenticationNumber, 5, TimeUnit.MINUTES);

		// 다형성을 이용한 메시지 전송 (SMS, EMAIL)
		MessageDto messageDto;
		if (numberReq.getMethod().equals(MessageSendMethod.SMS)) {
			messageDto = new SmsMessageDto(messageText, numberReq.getMethodInfo());
			System.out.println("[문자메세지]" + messageDto.toString());
		} else {
			messageDto = new EmailMessageDto(messageText, numberReq.getMethodInfo());
			System.out.println("[이메일메세지]" + messageDto.toString());
		}
		kafkaMessagingService.produceMessaging(messageDto);

		// Redis에서 저장된 값 확인
		String storedValue = redisTemplate.opsForValue().get(key);
		System.out.println("<-----Redis 저장 확인---->");
		System.out.println("Key: " + key);
		System.out.println("Value: " + storedValue);
	}


	public boolean isExistUsername(String username) {
		return vendorCustomRepository.isExistUsername(username);
	}

	/*
	 * 고객 Username 존재여부
	 * */
	private void validateVendorUser(String username) {
		if(!vendorCustomRepository.isExistUsername(username)) {
			throw new EntityNotFoundException("고객 없음(" + username + ")");
		}
	}
}
