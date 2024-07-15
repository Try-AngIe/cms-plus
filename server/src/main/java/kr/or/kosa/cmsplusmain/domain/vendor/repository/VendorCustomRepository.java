package kr.or.kosa.cmsplusmain.domain.vendor.repository;


import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import kr.or.kosa.cmsplusmain.domain.base.repository.BaseCustomRepository;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;

import static kr.or.kosa.cmsplusmain.domain.vendor.entity.QVendor.vendor;

@Repository
public class VendorCustomRepository extends BaseCustomRepository<Vendor> {

	public VendorCustomRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
		super(em, jpaQueryFactory);
	}

	/*
	 * 이미 존재하는 아이디인 경우: true
	 * */
	public boolean isExistUsername(String username) {
		Integer count = jpaQueryFactory
			.selectOne()
			.from(vendor)
			.where(vendor.username.eq(username), vendor.deleted.eq(false))
			.fetchFirst();

		return count != null;
	}

	public Vendor findByUsername(String username) {
		return jpaQueryFactory
			.selectFrom(vendor)
			.where(vendor.username.eq(username), vendor.deleted.eq(false))
			.fetchOne();
	}
}
