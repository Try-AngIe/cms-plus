package kr.or.kosa.cmsplusmain.domain.vendor.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.Identifier.IdFindReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.Identifier.IdFindRes;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.RefreshTokenRes;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.SignupReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.authenticationNumber.NumberReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.password.PwFindReq;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.password.PwResetReq;
import kr.or.kosa.cmsplusmain.domain.vendor.jwt.JWTUtil;
import kr.or.kosa.cmsplusmain.domain.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vendor/auth")
@RequiredArgsConstructor
public class VendorController {
	private final VendorService vendorService;
	private final JWTUtil jwtUtil;

	/*
	 * 회원 가입
	 * */
	@PostMapping("/join")
	public ResponseEntity<String> join(@RequestBody @Valid SignupReq signupReq) {
		try {
			vendorService.join(signupReq);
			return ResponseEntity.status(HttpStatus.CREATED).body("Signup successful.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	/*
	 * 아이디 중복체크
	 * */
	@GetMapping("/check-username")
	public ResponseEntity<Boolean> isExistUsername(@RequestParam String username) {
		boolean isExist = vendorService.isExistUsername(username);
		return ResponseEntity.ok(isExist);
	}

	/*
	 * 리프레쉬 토큰 발급
	 * */
	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
		try {
			RefreshTokenRes refreshTokenRes = vendorService.refresh(request, response);
			return ResponseEntity.ok(refreshTokenRes);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while refreshing token");
		}
	}

	/*
	 * 아이디 찾기
	 * */
	@PostMapping("/id-inquiry")
	public IdFindRes findIdentifier(@RequestBody @Valid IdFindReq idFindReq) {
		return vendorService.findIdentifire(idFindReq);
	}

	/*
	 * 비밀번호 찾기
	 * */
	@PostMapping("/pw-inquiry")
	public boolean findPassword(@RequestBody @Valid PwFindReq pwFindReq) {
		return vendorService.findPassword(pwFindReq);
	}

	/*
	 * 비밀번호 재설정
	 * */
	@PostMapping("/pw-reset")
	public void resetPassword(@RequestBody @Valid PwResetReq pwResetReq) {
		vendorService.resetPassword(pwResetReq);
	}

	/*
	 * 인증번호 요청
	 * */
	@PostMapping("/request-number")
	public void requestAuthenticationNumber(@RequestBody @Valid NumberReq numberReq) {
		vendorService.requestAuthenticationNumber(numberReq);
	}
}
