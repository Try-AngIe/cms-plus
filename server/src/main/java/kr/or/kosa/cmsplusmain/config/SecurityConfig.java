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
//				.requestMatchers("/api/v1/**","/**").permitAll()
				.requestMatchers("/api/v1/vendor/auth/login").permitAll()
				.requestMatchers("/api/v1/vendor/auth/join").permitAll()
				.requestMatchers("/api/v1/vendor/auth/logout").permitAll()
				.requestMatchers("/api/v1/vendor/auth/refresh").permitAll()
				.requestMatchers("/api/v1/vendor/auth/check-username").permitAll()
				.requestMatchers("/api/v1/vendor/auth/id-inquiry").permitAll()
				.requestMatchers("/api/v1/vendor/auth/pw-inquiry").permitAll()
				.requestMatchers("/api/v1/vendor/auth/pw-reset").permitAll()
				.requestMatchers("/api/v1/vendor/auth/request-number").permitAll()
				.requestMatchers("/api/v1/vendor/simple-consent/available-options/{vendorId}").permitAll()
				.requestMatchers("/api/v1/vendor/billing/invoice/{billingId}").permitAll()
				.requestMatchers("/api/v2/vendor/billing/invoice/{billingId}").permitAll()
				.requestMatchers("/api/v1/simple-consent/**").permitAll()
				.requestMatchers("/api/v1/simple-consent").permitAll()
				.requestMatchers("/api/v1/simple-consent/card/verify").permitAll()
				.requestMatchers("/api/v1/simple-consent/sign").permitAll()
				.requestMatchers("/api/v1/kafka/payment/card").permitAll()
				.requestMatchers("/api/v1/kafka/payment/account").permitAll()
				.requestMatchers("/api/v1/kafka/payment/virtual-account").permitAll()
				.requestMatchers("/api/v1/payment/banks").permitAll()
				.requestMatchers("/infra-test").permitAll()
				.requestMatchers("/actuator/prometheus").permitAll()
				.requestMatchers("/api/v1/kafka/messaging/sms").permitAll()
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
