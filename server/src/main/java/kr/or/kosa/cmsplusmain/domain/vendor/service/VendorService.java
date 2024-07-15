package kr.or.kosa.cmsplusmain.domain.vendor.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.PaymentType;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.repository.ProductCustomRepository;
import kr.or.kosa.cmsplusmain.domain.settings.entity.SimpConsentSetting;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kosa.cmsplusmain.domain.vendor.jwt.JWTUtil;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.RefreshTokenRes;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.SignupDto;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.UserRole;
import kr.or.kosa.cmsplusmain.domain.vendor.repository.VendorCustomRepository;
import kr.or.kosa.cmsplusmain.domain.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VendorService {
	private final VendorCustomRepository vendorCustomRepository;
	private final ProductCustomRepository productCustomRepository;
	private final VendorRepository vendorRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JWTUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;

	@Transactional
	public void join(SignupDto signupDto) {
		String username = signupDto.getUsername();
		String password = bCryptPasswordEncoder.encode(signupDto.getPassword());
		UserRole role = UserRole.ROLE_VENDOR;

		// 중복된 아이디가 입력된 경우 예외처리
		// Custom exception도 좋고
		//

		boolean isExist = vendorCustomRepository.isExistUsername(username);
		if (isExist) {
			throw new IllegalArgumentException("Username already exists.");
		}

		// TODO
		// 상품 하나 추가
		// 상품을 토대로 기본 설정 추가
		//vendorRepository.save(signupDto.toEntity(username, password, role));

		Vendor vendor = signupDto.toEntity(username, password, role);

		// 간편동의 설정 초기화
		SimpConsentSetting simpConsentSetting = createDefaultSimpConsentSetting();
		vendor.setSimpConsentSetting(simpConsentSetting);

		vendorRepository.save(vendor);
	}

	// 간편동의 설정 기본값
	private SimpConsentSetting createDefaultSimpConsentSetting() {
		SimpConsentSetting setting = SimpConsentSetting.builder().build();

		// 전체 자동결제 수단 추가
		Set<PaymentMethod> autoPaymentMethods = new HashSet<>(PaymentType.getAutoPaymentMethods());
		for (PaymentMethod paymentMethod : autoPaymentMethods) {
			setting.addPaymentMethod(paymentMethod);
		}

		// ProductCustomRepository를 사용하여 상품 조회
		//  전체 상품 조회
		List<Product> defaultProducts = productCustomRepository.findProducts();

		for (Product product : defaultProducts) {
			setting.addProduct(product);
		}

		return setting;
	}

	@Transactional
	public RefreshTokenRes refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String authorizationRefresh = request.getHeader("Authorization-refresh");

		// authorizationRefresh 헤더 검증
		if (authorizationRefresh == null || !authorizationRefresh.startsWith("Bearer ")) {
			throw new IllegalArgumentException("refresh token null");
		}

		String refreshToken = authorizationRefresh.split(" ")[1];

		// 토큰 만료 여부 확인
		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			throw new IllegalArgumentException("Invaild refresh token");
		}

		String category = jwtUtil.getCategory(refreshToken);

		// 토큰이 refresh인지 확인
		if (!category.equals("refresh")) {
			throw new IllegalArgumentException("Invalid refresh token");
		}

		// 토큰이 Redis에 저장되어 있는지 확인
		String storedRefreshToken = redisTemplate.opsForValue().get(jwtUtil.getUsername(refreshToken));
		if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
			throw new IllegalArgumentException("Invalid refresh token");
		}

		String username = jwtUtil.getUsername(refreshToken);
		String role = jwtUtil.getRole(refreshToken);

		// JWT 토큰 생성
		String newAccessToken = jwtUtil.createJwt("access", username, role, 10 * 60 * 1000L);
		String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

		// 기존 토큰을 Redis에서 제거 후 새로운 토큰 저장
		redisTemplate.delete(username);
		redisTemplate.opsForValue().set(username, newRefreshToken, 14, TimeUnit.DAYS);

		// Json 응답 본문 작성
		return RefreshTokenRes.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.build();
	}

	public boolean isExistUsername(String username) {
		return vendorCustomRepository.isExistUsername(username);
	}
}
