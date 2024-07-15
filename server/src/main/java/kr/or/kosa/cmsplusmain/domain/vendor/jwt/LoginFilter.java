package kr.or.kosa.cmsplusmain.domain.vendor.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.AccessTokenRes;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.LoginDto;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.VendorUserDetailsDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;

	{
		setFilterProcessesUrl("/api/v1/vendor/auth/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {

		LoginDto loginDto;
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);
		} catch (IOException e) {
			throw new RuntimeException("Invaild login request format", e);
		}

		String username = loginDto.getUsername();
		String password = loginDto.getPassword();

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,
			null);
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException {
		VendorUserDetailsDto vendorUserDetails = (VendorUserDetailsDto)authentication.getPrincipal();
		String username = vendorUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		// JWT 토큰 생성
		String accessToken = jwtUtil.createJwt("access", username, role, 30 * 60 * 1000L);
		String refreshToken = jwtUtil.createJwt("refresh", username, role, 14 * 24 * 60 * 60 * 1000L);

		// Redis에 저장
		redisTemplate.opsForValue().set(username, refreshToken, 14, TimeUnit.DAYS);

		// 저장된 토큰 확인
		String savedToken = redisTemplate.opsForValue().get(username);
		logger.info("Saved RedisToken for username {}: {}", username, savedToken);

		// 응답 본문 작성
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);

		// JSON 응답 본문 작성
		AccessTokenRes accessTokenRes = AccessTokenRes.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.role(role.replace("ROLE_", ""))
			.build();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getWriter(), accessTokenRes);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
