package kr.or.kosa.cmsplusmain.domain.vendor.repository;

import org.springframework.stereotype.Repository;

import kr.or.kosa.cmsplusmain.domain.base.repository.BaseRepository;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;

@Repository
public interface VendorRepository extends BaseRepository<Vendor, Long> {
}
