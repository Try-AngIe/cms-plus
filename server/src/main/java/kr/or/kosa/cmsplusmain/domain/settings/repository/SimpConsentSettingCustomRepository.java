package kr.or.kosa.cmsplusmain.domain.settings.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.or.kosa.cmsplusmain.domain.base.repository.BaseCustomRepository;
import kr.or.kosa.cmsplusmain.domain.settings.entity.SimpConsentSetting;
import org.springframework.stereotype.Repository;

import static kr.or.kosa.cmsplusmain.domain.settings.entity.QSimpConsentSetting.simpConsentSetting;
import static kr.or.kosa.cmsplusmain.domain.vendor.entity.QVendor.vendor;


@Repository
public class SimpConsentSettingCustomRepository extends BaseCustomRepository<SimpConsentSetting> {

    public SimpConsentSettingCustomRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
        super(em, jpaQueryFactory);
    }

    public SimpConsentSetting findByVendorUsername(String username) {
        return jpaQueryFactory
                .selectFrom(simpConsentSetting)
                .join(simpConsentSetting.vendor, vendor)
                .where(
                        vendorUsernameEq(username),
                        simpConsentSetting.deleted.isFalse()
                )
                .fetchOne();
    }
}