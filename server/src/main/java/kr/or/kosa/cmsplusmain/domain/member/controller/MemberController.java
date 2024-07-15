package kr.or.kosa.cmsplusmain.domain.member.controller;

import kr.or.kosa.cmsplusmain.domain.base.dto.SortPageDto;
import kr.or.kosa.cmsplusmain.domain.contract.dto.MemberContractListItemDto;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberDetail;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberListItem;
import kr.or.kosa.cmsplusmain.domain.member.service.MemberService;
import kr.or.kosa.cmsplusmain.domain.vendor.dto.VendorUserDetailsDto;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vendor/management")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /*
     * 회원 목록 조회
     * */
    @GetMapping("/members")
    public SortPageDto.Res<MemberListItem> getMemberList(@AuthenticationPrincipal VendorUserDetailsDto userDetails, SortPageDto.Req pageable) {
//        String username = userDetails.getUsername();
        String username = "vendor1";
        return memberService.findMemberListItem(username, pageable);
    }

    /*
     * 회원 상세 - 기본 정보 조회
     * */
    @GetMapping("/members/{memberId}")
    public MemberDetail getMemberContractList(@AuthenticationPrincipal VendorUserDetailsDto userDetails, @PathVariable Long memberId) {
//        String username = userDetails.getUsername();
        String username = "vendor1";
        return memberService.findMemberDetailById(username, memberId);
    }

    /*
     * 회원 상세 - 계약 리스트 조회
     * */
    @GetMapping("/members/contracts/{memberId}")
    public SortPageDto.Res<MemberContractListItemDto> getMemberContractList(@AuthenticationPrincipal VendorUserDetailsDto userDetails, @PathVariable Long memberId , SortPageDto.Req pageable) {
//        String username = userDetails.getUsername();
        String username = "vendor1";
        return memberService.findContractListItemByMemberId(username, memberId, pageable);
    }
}
