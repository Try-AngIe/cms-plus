package kr.or.kosa.cmsplusmain.domain.messaging.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SmsRepository {

	// Redis 데이터 설정
	private final String PREFIX = "SMS-"; // 저장되는 키 값은 "SMS-01011111111" 형태
	private final int LIMIT_TIME = 5 * 60; // 300초(5분)동안 캐싱

	private final StringRedisTemplate redisTemplate;

	public void createSmsVerification(String phone, String certificationNumber) {
		redisTemplate.opsForValue()
			.set(PREFIX + phone, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
	}

	public String getSmsVerification(String phone) {
		return redisTemplate.opsForValue().get(PREFIX + phone);
	}

	public void removeSmsCertification(String phone) {
		redisTemplate.delete(PREFIX + phone);
	}

	public boolean hasKey(String phone) {
		return redisTemplate.hasKey(PREFIX + phone);
	}

}
