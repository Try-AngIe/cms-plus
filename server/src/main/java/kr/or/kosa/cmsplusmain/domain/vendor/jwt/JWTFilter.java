package kr.or.kosa.cmsplusmain.domain.vendor.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.VendorUserDetailsDto;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.UserRole;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String authorization = request.getHeader("Authorization");

		// Authorization 헤더 검증
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = authorization.split(" ")[1];

		// 토큰 만료 여부 확인, 만료시 다름 필터로 넘기지 않음
		try {
			jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {

			// response body
			PrintWriter writer = response.getWriter();
			writer.print("access token expired");

			// response status code
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String category = jwtUtil.getCategory(accessToken);

		// 토큰이 access인지 확인
		if (!category.equals("access")) {

			// response body
			PrintWriter writer = response.getWriter();
			writer.print("invalid access token");

			// response status code
			System.out.println("token expired");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String username = jwtUtil.getUsername(accessToken);
		String role = jwtUtil.getRole(accessToken);

		Vendor vendor = Vendor.builder()
			.username(username)
			.role(UserRole.valueOf(role))
			.build();

		VendorUserDetailsDto vendorUserDetailsDto = new VendorUserDetailsDto(vendor);

		Authentication authToken = new UsernamePasswordAuthenticationToken(vendorUserDetailsDto, null,
			vendorUserDetailsDto.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}
