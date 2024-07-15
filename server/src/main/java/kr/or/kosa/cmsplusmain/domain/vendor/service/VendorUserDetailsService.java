package kr.or.kosa.cmsplusmain.domain.vendor.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.or.kosa.cmsplusmain.domain.vendor.dto.VendorUserDetailsDto;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import kr.or.kosa.cmsplusmain.domain.vendor.repository.VendorCustomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorUserDetailsService implements UserDetailsService {

	private final VendorCustomRepository vendorRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Vendor userData = vendorRepository.findByUsername(username);

		if (userData != null) {
			return new VendorUserDetailsDto(userData);
		}

		return null;
	}
}
