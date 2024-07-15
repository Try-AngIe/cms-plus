package kr.or.kosa.cmsplusmain.domain.member.service;

import jakarta.persistence.EntityNotFoundException;
import kr.or.kosa.cmsplusmain.domain.base.dto.SortPageDto;
import kr.or.kosa.cmsplusmain.domain.billing.repository.BillingCustomRepository;
import kr.or.kosa.cmsplusmain.domain.contract.dto.MemberContractListItemDto;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractCustomRepository;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberDetail;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberListItem;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.member.repository.MemberCustomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberCustomRepository memberCustomRepository;
    private final ContractCustomRepository contractCustomRepository;
    private final BillingCustomRepository billingCustomRepository;

    /*
     * 회원 목록 조회
     * */
    public SortPageDto.Res<MemberListItem> findMemberListItem (String username, SortPageDto.Req pageable) {

        int countMemberListItem = memberCustomRepository.countAllMemberByVendor(username);

        int totalPages = (int) Math.ceil((double) countMemberListItem / pageable.getSize());

        List<MemberListItem> memberListItem = memberCustomRepository
                .findAllMemberByVendor(username, pageable)
                .stream()
                .map(MemberListItem::fromEntity)
                .toList();

        return new SortPageDto.Res<>(totalPages, memberListItem);
    }

    /*
    *  회원 상세 - 기본정보
    * */
    public MemberDetail findMemberDetailById(String username, Long memberId) {

        // 회원 정보 조회
        Member member = memberCustomRepository.findMemberDetailById(username, memberId)
                        .orElseThrow(() -> new EntityNotFoundException("회원 ID 없음(" + memberId + ")"));

        // 청구서 수
        int billingCount = billingCustomRepository.findBillingStandardByMemberId(username, memberId);

        // 총 청구 금액
        Long totalBillingPrice = billingCustomRepository.findBillingProductByMemberId(username, memberId);

        return MemberDetail.fromEntity(member, billingCount, totalBillingPrice);
    }

    /*
    * 회원 상세 - 계약리스트
    * */
    public SortPageDto.Res<MemberContractListItemDto> findContractListItemByMemberId(String username, Long memberId, SortPageDto.Req pageable) {

        int countAllContractListItemByMemberId = contractCustomRepository.countContractListItemByMemberId(username, memberId);
        int totalPages = (int) Math.ceil((double) countAllContractListItemByMemberId / pageable.getSize());

        List<MemberContractListItemDto> memberContractListItemDtos = contractCustomRepository
                .findContractListItemByMemberId(username, memberId, pageable)
                .stream()
                .map(MemberContractListItemDto::fromEntity)
                .toList();

        return new SortPageDto.Res<>(totalPages, memberContractListItemDtos);
    }
}
