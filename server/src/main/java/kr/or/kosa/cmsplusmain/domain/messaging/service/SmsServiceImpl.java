package kr.or.kosa.cmsplusmain.domain.messaging.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.kosa.cmsplusmain.domain.messaging.dto.SmsVerifyDto;
import kr.or.kosa.cmsplusmain.domain.messaging.repository.SmsRepository;
import kr.or.kosa.cmsplusmain.util.SmsUtil;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

	private final SmsUtil smsUtil;
	private final SmsRepository smsRepository;

	public void sendSmsCode(SmsVerifyDto smsVerifyDto) {
		String to = smsVerifyDto.getPhone();
		int randomNumber = (int)(Math.random() * 9000) + 1000;
		String certificationNumber = String.valueOf(randomNumber);
		smsUtil.sendSms(to, certificationNumber);
		smsRepository.createSmsVerification(to, certificationNumber);
	}

	public void verifySmsCode(SmsVerifyDto smsVerifyDto) {
		if (!isVerify(smsVerifyDto)) {
			throw new IllegalArgumentException("[SMS] 인증번호 불일치");
		}
		smsRepository.removeSmsCertification(smsVerifyDto.getPhone());
	}

	public boolean isVerify(SmsVerifyDto smsVerifyDto) {
		return smsRepository.hasKey(smsVerifyDto.getPhone()) &&
			smsRepository.getSmsVerification(smsVerifyDto.getPhone())
				.equals(smsVerifyDto.getSmsVerifyCode());
	}

}
