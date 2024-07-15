package kr.or.kosa.cmsplusmain.config;

import kr.or.kosa.cmsplusmain.domain.vendor.jwt.CustomLogoutFilter;
import kr.or.kosa.cmsplusmain.domain.vendor.jwt.JWTFilter;
import kr.or.kosa.cmsplusmain.domain.vendor.jwt.JWTUtil;
import kr.or.kosa.cmsplusmain.domain.vendor.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;



import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.cors(Customizer.withDefaults());

		//csrf disable
		http.csrf((auth) -> auth.disable());

		//From 로그인 방식 disable
		http.formLogin((auth) -> auth.disable());

		//http basic 인증 방식 disable
		http.httpBasic((auth) -> auth.disable());

		http.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/api/v1/vendor/auth/login").permitAll()
				.requestMatchers("/api/v1/vendor/auth/join").permitAll()
				.requestMatchers("/api/v1/vendor/auth/logout").permitAll()
				.requestMatchers("/api/v1/vendor/auth/refresh").permitAll()
				.requestMatchers("/api/v1/vendor/auth/username-check").permitAll()
				.requestMatchers("/api/v1/**","/**").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/vendor").hasRole("VENDOR")
				.requestMatchers("/member").hasRole("MEMBER")

				.anyRequest().authenticated());

		http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

		http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, redisTemplate),
				UsernamePasswordAuthenticationFilter.class);

		http.addFilterBefore(new CustomLogoutFilter(jwtUtil, redisTemplate), LogoutFilter.class);

		//세션 설정
		http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
