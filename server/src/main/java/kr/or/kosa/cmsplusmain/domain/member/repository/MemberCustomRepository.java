package kr.or.kosa.cmsplusmain.domain.member.repository;

import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.*;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContractProduct.*;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.repository.BaseCustomRepository;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberSearchReq;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;


@Repository
public class MemberCustomRepository extends BaseCustomRepository<Member> {
    public MemberCustomRepository(EntityManager em, JPAQueryFactory jpaQueryFactory, JdbcTemplate jdbcTemplate) {
        super(em, jpaQueryFactory);
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    /**
    * 회원 목록 조회
    * 기본 정렬: 최신 등록된 회원(회원 등록일 기준, not db 생성)
    * 4회 쿼리발생 | 계약카운트, 회원 정보 조회, 계약 정보 조회, 계약 상품 정보 조회
    * */
    public List<Member> searchAllMemberByVendor(Long vendorId, MemberSearchReq memberSearch, PageReq pageable) {
        return jpaQueryFactory
                .selectFrom(member)
                .leftJoin(member.contracts, contract).on(contract.deleted.isFalse())
                .leftJoin(contract.contractProducts, contractProduct).on(contractProduct.deleted.isFalse())
                .where(
                        member.vendor.id.eq(vendorId),
                        memberNotDel(),
                        memberNameContains(memberSearch.getMemberName()),
                        memberPhoneContains(memberSearch.getMemberPhone()),
                        memberEmailContains(memberSearch.getMemberEmail()),
                        memberEnrollDateEq(memberSearch.getMemberEnrollDate())
                )
                .groupBy(member.id)
                .having(
                        contractNumberLoe(memberSearch.getContractCount()),
                        contractPriceLoeInGroup(memberSearch.getContractPrice())
                )
                .orderBy(buildMemberOrderSpecifier(pageable))
                .offset(pageable.getPage())
                .limit(pageable.getSize())
                .fetch();
    }

    /**
     * 전체 회원 수
     * */
    public int countAllMemberByVendor(Long vendorId, MemberSearchReq memberSearch) {
        JPQLQuery<Long> subquery = JPAExpressions
                .select(member.id)
                .from(member)
                .leftJoin(member.contracts, contract).on(contract.deleted.isFalse())
                .leftJoin(contract.contractProducts, contractProduct).on(contractProduct.deleted.isFalse())
                .where(
                    member.vendor.id.eq(vendorId),
                    memberNotDel(),

                    memberNameContains(memberSearch.getMemberName()),
                    memberPhoneContains(memberSearch.getMemberPhone()),
                    memberEmailContains(memberSearch.getMemberEmail()),
                    memberEnrollDateEq(memberSearch.getMemberEnrollDate())
                )
                .groupBy(member.id)
                .having(
                        contractNumberLoe(memberSearch.getContractCount()),
                        contractPriceLoeInGroup(memberSearch.getContractPrice())
                );

        Long res = jpaQueryFactory.select(member.id.count())
                .from(member)
                .where(member.id.in(subquery))
                .fetchOne();

        return (res != null) ? res.intValue() : 0;
    }

    /**
     * 회원 기본정보 목록
     * */
    public List<Member> findAllMembers(Long vendorId, MemberSearchReq memberSearch, PageReq pageReq) {
        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.vendor.id.eq(vendorId),
                member.deleted.isFalse(),
                memberIdContains(memberSearch.getMemberId()),
                memberNameContains(memberSearch.getMemberName()),
                memberPhoneContains(memberSearch.getMemberPhone())
            )
            .offset(pageReq.getPage())
            .limit(pageReq.getSize())
            .fetch();
    }

    /**
     * 회원 기본정보 목록 개수
     * */
    public int countAllMembers(Long vendorId, MemberSearchReq memberSearch) {
        Long res = jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(
                member.vendor.id.eq(vendorId),
                member.deleted.isFalse(),
                memberIdContains(memberSearch.getMemberId()),
                memberNameContains(memberSearch.getMemberName()),
                memberPhoneContains(memberSearch.getMemberPhone())
            )
            .fetchOne();

        return (res != null) ? res.intValue() : 0;
    }

    /**
    * 회원 상세 조회 : 기본정보
    * */
    public Optional<Member> findMemberDetailById(Long vendorId, Long memberId){
        return Optional.ofNullable(
            jpaQueryFactory
            .selectFrom(member)
            .where(
                    member.vendor.id.eq(vendorId),
                    member.id.eq(memberId),
                    memberNotDel()
            )
            .fetchOne());
    }

    /**
     * 회원 존재 여부 판단 번호
     * */
    @Deprecated
    public boolean idExistMemberByPhone(Long vendorId, String phone) {
        Integer fetchOne = jpaQueryFactory
            .selectOne()
            .from(member)
            .where(
                    member.vendor.id.eq(vendorId),
                    member.phone.eq(phone),
                    memberNotDel()
            )
            .fetchFirst();

        return fetchOne != null;
    }

    /**
     * 회원 존재 여부 판단 이메일
     * */
    @Deprecated
    public boolean idExistMemberByEmail(Long vendorId, String email) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(member)
                .where(
                        member.vendor.id.eq(vendorId),
                        member.email.eq(email),
                        memberNotDel()
                )
                .fetchFirst();

        return fetchOne != null;
    }


    /**
    * 회원 존재 여부 판단 번호와 이메일
    * */
    public boolean canSaveMember(String phone, String email) {
        if (!StringUtils.hasText(phone) && !StringUtils.hasText(email)) return true;

        Integer res = jpaQueryFactory
            .selectOne()
            .from(member)
            .where(
                memberPhoneOrEmailEq(phone, email),
                memberNotDel()
            )
            .fetchOne();

        return res == null;
    }

    private BooleanExpression memberPhoneOrEmailEq(String phone, String email) {
        BooleanExpression memberPhoneEq = memberPhoneEq(phone);
        if (memberPhoneEq == null) {
            return memberEmailEq(email);
        }
        return memberPhoneEq.or(memberEmailEq(email));
    }

    public Optional<Member> findMemberByPhone(Long vendorId, String phone) {
        return Optional.ofNullable(
            jpaQueryFactory
                .selectFrom(member)
                .where(
                        member.vendor.id.eq(vendorId),
                        member.phone.eq(phone),
                        memberNotDel()
                )
                .fetchOne());
    }

    /**
     * 회원 존재 여부 판단 - by 회원 Id
     */
    public boolean isExistMemberById(Long memberId, Long vendorId) {
        Integer res = jpaQueryFactory
            .selectOne()
            .from(member)
            .where(
                member.id.eq(memberId),
                member.vendor.id.eq(vendorId)
            )
            .fetchOne();
        return res != null;
    }

    /**
     * 대량 회원 등록을 위한 bulk insert
     * */
    @Transactional
    public void bulkInsert(List<Member> members) {
        String sql = "INSERT INTO member (deleted, member_auto_billing, member_auto_invoice_send, " +
            "member_enroll_date, created_datetime, modified_datetime, vendor_id, member_home_phone, member_phone, " +
            "member_name, member_email, member_memo, zipcode, address, address_detail, " +
            "member_invoice_send_method, member_status, member_contract_price, member_contract_count) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Member member = members.get(i);
                ps.setBoolean(1, member.isDeleted());
                ps.setBoolean(2, member.isAutoBilling());
                ps.setBoolean(3, member.isAutoInvoiceSend());
                ps.setDate(4, java.sql.Date.valueOf(member.getEnrollDate()));
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setLong(7, member.getVendor().getId());
                ps.setString(8, member.getHomePhone());
                ps.setString(9, member.getPhone());
                ps.setString(10, member.getName());
                ps.setString(11, member.getEmail());
                ps.setString(12, member.getMemo());
                ps.setString(13, member.getAddress().getZipcode());
                ps.setString(14, member.getAddress().getAddress());
                ps.setString(15, member.getAddress().getAddressDetail());
                ps.setString(16, member.getInvoiceSendMethod().name());
                ps.setString(17, member.getStatus().name());
            }

            @Override
            public int getBatchSize() {
                // SQL 최대 길이 제한
                return Math.min(members.size(), 500);
            }
        });
    }

    private OrderSpecifier<?> buildMemberOrderSpecifier(PageReq pageReq) {
        if (pageReq == null || !StringUtils.hasText(pageReq.getOrderBy())) {
            return member.createdDateTime.desc();
        }

        String orderBy = pageReq.getOrderBy();

        if (orderBy.equals("contractPrice")) {
            NumberExpression<Long> exp = contractProduct.price.longValue().multiply(contractProduct.quantity).sum();
            return pageReq.isAsc() ? exp.asc() : exp.desc();
        }

        if (orderBy.equals("contractCount")) {
            NumberExpression<Long> exp = contract.countDistinct();
            return pageReq.isAsc() ? exp.asc() : exp.desc();
        }

        if (orderBy.equals("memberName")) {
            StringExpression exp = member.name;
            return pageReq.isAsc() ? exp.asc() : exp.desc();
        }
        return member.createdDateTime.desc();
    }
    private BooleanExpression memberEmailEq(String email) {
        return StringUtils.hasText(email) ?  member.email.eq(email) : null;
    }
    private BooleanExpression memberIdContains(Long memberId) {
        return (memberId != null) ?  member.id.stringValue().contains(memberId.toString()) : null;
    }

    private BooleanExpression memberPhoneEq(String phone) {
        return StringUtils.hasText(phone) ? member.phone.eq(phone) : null;
    }

    private BooleanExpression memberEmailContains(String memberEmail) {
        return StringUtils.hasText(memberEmail) ? member.email.contains(memberEmail) : null;
    }

    private BooleanExpression memberEnrollDateEq(LocalDate memberEnrollDate) {
        return (memberEnrollDate != null) ? member.enrollDate.eq(memberEnrollDate) : null;
    }



    private BooleanExpression contractNumberLoe(Integer contractNumber) {
        if (contractNumber == null) {
            return null;
        }
        return contract.countDistinct().loe(contractNumber);
    }
    private BooleanExpression contractPriceLoe(Long contractPrice) {
        if (contractPrice == null) {
            return null;
        }
        return contractProduct.price.longValue().multiply(contractProduct.quantity).loe(contractPrice);
    }
}