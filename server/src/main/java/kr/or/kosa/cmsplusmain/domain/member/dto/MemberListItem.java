package kr.or.kosa.cmsplusmain.domain.member.dto;

import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MemberListItem {

    private final Long memberId;              // 회원 ID
    private final String memberName;          // 회원 이름
    private final String memberPhone;         // 회원 휴대전화
    private final String memberEmail;         // 회원 이메일
    private final LocalDate memberEnrollDate; // 회원 등록일
    private final Long contractPrice;         // 총 계약 금액
    private final Integer contractCount;      // 계약 개수

    public static MemberListItem fromEntity(Member member){
        final List<Contract> contracts = member.getContracts();

        return MemberListItem.builder()
                .memberId(member.getId())
                .memberName(member.getName())
                .memberPhone(member.getPhone())
                .memberEmail(member.getEmail())
                .memberEnrollDate(member.getEnrollDate())
                .contractPrice(member.totalContractPrice())
                .contractCount(contracts.size())
                .build();

    }
}
