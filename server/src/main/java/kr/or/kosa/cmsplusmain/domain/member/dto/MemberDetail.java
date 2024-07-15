package kr.or.kosa.cmsplusmain.domain.member.dto;

import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MemberDetail {

    private final Long memberId;                  // 회원 ID
    private final String memberName;              // 회원 이름
    private final String memberPhone;             // 회원 휴대전화
    private final String memberEmail;             // 회원 이메일
    private final String memberHomePhone;         // 회원 자택 전화번호
    private final String memberAddress;           // 회원 주소
    private final String memberMemo;              // 회원 메모
    private final LocalDate memberEnrollDate;     // 회원 등록일
    private final LocalDateTime createdDateTime;  // 생성 일시
    private final LocalDateTime modifiedDateTime; // 수정 일시
    private final int contractCount;              // 계약 수
    private final int billingCount;               // 청구서 수
    private final Long totalBillingPrice;         // 총 청구 금액


    public static MemberDetail fromEntity(Member member,int billingCount, Long totalBillingPrice){

        List<Contract> contracts = member.getContracts();

        return MemberDetail.builder()
                .memberId(member.getId())
                .memberName(member.getName())
                .memberPhone(member.getPhone())
                .memberEmail(member.getEmail())
                .memberHomePhone(member.getHomePhone())
                .memberAddress(member.getAddress())
                .memberMemo(member.getMemo())
                .memberEnrollDate(member.getEnrollDate())
                .createdDateTime(member.getCreatedDateTime())
                .modifiedDateTime(member.getModifiedDateTime())
                .contractCount(contracts.size())
                .billingCount(billingCount)
                .totalBillingPrice(totalBillingPrice)
                .build();
    }
}
