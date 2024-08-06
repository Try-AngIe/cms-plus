package kr.or.kosa.cmsplusmain.domain.member.service;

import com.querydsl.jpa.impl.JPAQuery;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberListItemRes;
import kr.or.kosa.cmsplusmain.domain.member.dto.MemberSearchReq;
import kr.or.kosa.cmsplusmain.domain.member.repository.V2MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class V2MemberService {

    private final V2MemberRepository memberRepository;

    /**
     * 회원 목록 조회
     * */
    public PageRes<MemberListItemRes> searchMembers(Long vendorId, MemberSearchReq search, PageReq pageReq) {

        List<MemberListItemRes> content = memberRepository.searchMembers(vendorId,search,pageReq);

        int totalContentCount = memberRepository.countSearchedMembers(vendorId,search).intValue();

        return new PageRes<>(totalContentCount,pageReq.getSize(),content);
    }
}
