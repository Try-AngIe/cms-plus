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

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.or.kosa.cmsplusmain.domain.vendor.jwt.JWTUtil;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.RefreshTokenRes;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.SignupDto;
import kr.or.kosa.cmsplusmain.domain.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vendor/auth")
@RequiredArgsConstructor
public class VendorController {
	private final VendorService vendorService;
	private final JWTUtil jwtUtil;

	@PostMapping("/join")
	public ResponseEntity<String> join(@RequestBody @Valid SignupDto signupDto) {
		try {
			vendorService.join(signupDto);
			return ResponseEntity.status(HttpStatus.CREATED).body("Signup successful.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@GetMapping("/check-username")
	public ResponseEntity<Boolean> isExistUsername(@RequestParam String username) {
		boolean isExist = vendorService.isExistUsername(username);
		return ResponseEntity.ok(isExist);
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
		try {
			RefreshTokenRes refreshTokenRes = vendorService.refresh(request, response);

			// 응답 본문 작성
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(response.getWriter(), refreshTokenRes);

			return ResponseEntity.ok(refreshTokenRes);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while refreshing token");
		}
	}
}
