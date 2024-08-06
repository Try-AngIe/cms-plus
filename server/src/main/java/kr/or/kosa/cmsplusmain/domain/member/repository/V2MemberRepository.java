package kr.or.kosa.cmsplusmain.domain.member.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.repository.V2BaseRepository;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberListItemRes;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberSearchReq;
import kr.or.kosa.cmsplusmain.domain.member.dto.QMemberListItemRes;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.member;
import static kr.or.kosa.cmsplusmain.util.QueryExpressions.*;

@Repository
public class V2MemberRepository extends V2BaseRepository<Member, Long> {

    /**
     * 회원 목록 조회
     */
    public List<MemberListItemRes> searchMembers(Long vendorId, MemberSearchReq search, PageReq pageReq) {
        JPAQuery<MemberListItemRes> query = searchQuery(vendorId, search)
            .select(new QMemberListItemRes(
                    member.id,
                    member.name,
                    member.phone,
                    member.email,
                    member.enrollDate,
                    member.contractPrice,
                    member.contractCount
            )).orderBy(buildOrderSpecifier(pageReq));
        return applyPaging(query, pageReq).fetch();
    }

    /**
     *  회원 전체 수 (검색 조건 반영된 회원 수)
     */
    public Long countSearchedMembers(Long vendorId, MemberSearchReq search){
        Long res = searchQuery(vendorId, search)
                    .select(member.count())
                    .fetchOne();
        return res == null ? 0 : res;
    }

    /**
     * 회원 목록 검색 조건
     */
    private JPAQuery<?> searchQuery(Long vendorId, MemberSearchReq search) {
        return from(member)
            .where(
                member.vendor.id.eq(vendorId),
                memberNameContains(search.getMemberName()),
                memberPhoneContains(search.getMemberPhone()),
                memberEmailContains(search.getMemberEmail()),
                memberEnrollDateEq(search.getMemberEnrollDate()),
                memberContractPriceLoe(search.getContractPrice()),
                memberContractCountLoe(search.getContractCount())
            );
    }

    /**
     * 회원 목록 조건 정렬
     */
    private OrderSpecifier<?> buildOrderSpecifier(PageReq pageReq) {
        if(pageReq == null || !StringUtils.hasText(pageReq.getOrderBy())){
            return member.createdDateTime.desc();
        }

        String orderBy = pageReq.getOrderBy();

        switch (orderBy) {
            case "contractPrice" -> {
                NumberExpression<Long> expression = member.contractPrice;
                return pageReq.isAsc() ? expression.asc() : expression.desc();
            }
            case "contractCount" -> {
                NumberExpression<Integer> expression = member.contractCount;
                return pageReq.isAsc() ? expression.asc() : expression.desc();
            }
            case "memberName" -> {
                StringExpression expression = member.name;
                return pageReq.isAsc() ? expression.asc() : expression.desc();
            }
        }
        return member.createdDateTime.desc();
    }
}
