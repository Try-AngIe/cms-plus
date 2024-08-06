package kr.or.kosa.cmsplusmain.domain.member.controller;

import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.base.security.VendorId;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberListItemRes;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberSearchReq;
import kr.or.kosa.cmsplusmain.domain.member.service.V2MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v2/vendor/management")
@RequiredArgsConstructor
public class V2MemberController {

    private final V2MemberService memberService;

    /**
     * 회원 목록 조회
     * */
    @GetMapping("/members")
    public PageRes<MemberListItemRes> getMemberList(@VendorId Long vendorId, MemberSearchReq memberSearch, PageReq pageable) {
        return memberService.searchMembers(vendorId, memberSearch, pageable);
    }
}