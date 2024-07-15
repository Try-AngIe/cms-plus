package kr.or.kosa.cmsplusmain.domain.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.or.kosa.cmsplusmain.domain.base.dto.SortPageDto;
import kr.or.kosa.cmsplusmain.domain.base.repository.BaseCustomRepository;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.member;
import static kr.or.kosa.cmsplusmain.domain.vendor.entity.QVendor.vendor;


@Repository
public class MemberCustomRepository extends BaseCustomRepository<Member> {
    public MemberCustomRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
        super(em, jpaQueryFactory);
    }

    /*
    * 회원 목록 조회
    * */
    public List<Member> findAllMemberByVendor(String Username, SortPageDto.Req pageable) {
        return jpaQueryFactory
            .selectFrom(member)
            .join(member.vendor, vendor)
            .where(
                    vendorUsernameEq(Username),
                    memberNotDel()
            )
            .offset(pageable.getPage())
            .limit(pageable.getSize())
            .fetch();
    }

    /*
     * 전체 회원 수
     * */
    public int countAllMemberByVendor(String Username) {
        return jpaQueryFactory
            .select(member.id.countDistinct()).from(member)
            .join(member.vendor, vendor)
            .where(
                    vendorUsernameEq(Username),
                    memberNotDel()
            )
            .fetchOne().intValue();
    }

    /*
    * 회원 상세 조회 : 기본정보
    *
    *   select
            member1
        from
            Member member1
        inner join
            member1.vendor as vendor
        where
            vendor.username = ?1
            and member1.id = ?2
            and member1.deleted = ?3
    * */
    public Optional<Member> findMemberDetailById(String Username, Long memberId){
        return Optional.ofNullable(
            jpaQueryFactory
            .selectFrom(member)
            .join(member.vendor, vendor)
            .where(
                    vendorUsernameEq(Username),
                    member.id.eq(memberId),
                    memberNotDel()
            )
            .fetchOne());
    }
}