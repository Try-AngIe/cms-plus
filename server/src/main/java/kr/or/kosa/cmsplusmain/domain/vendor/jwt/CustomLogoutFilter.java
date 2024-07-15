package kr.or.kosa.cmsplusmain.domain.vendor.jwt;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
	private final JWTUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		IOException,
		ServletException {

		//Path 와 method 검증
		String requestUrl = request.getRequestURI();

		if (!requestUrl.matches("/api/v1/vendor/auth/logout")) {
			filterChain.doFilter(request, response);
			return;
		}

		String requestMethod = request.getMethod();

		if (!requestMethod.equals("DELETE")) {
			filterChain.doFilter(request, response);
		}

		// Refreah token 존재 여부 검증
		String authorizationRefresh = request.getHeader("Authorization-refresh");

		if (authorizationRefresh == null || !authorizationRefresh.startsWith("Bearer ")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// Refresh token 만료 기한 체크
		String refreshToken = authorizationRefresh.split(" ")[1];

		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		// 토큰이 Refresh인지 검증
		String category = jwtUtil.getCategory(refreshToken);

		if (!category.equals("refresh")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// Refresh 토큰이 Redis에 있는지 확인
		String storedRefreshToken = redisTemplate.opsForValue().get(jwtUtil.getUsername(refreshToken));

		if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		// 실제 로그아웃
		String username = jwtUtil.getUsername(refreshToken);
		redisTemplate.delete(username);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("Logout successful");
	}
}
