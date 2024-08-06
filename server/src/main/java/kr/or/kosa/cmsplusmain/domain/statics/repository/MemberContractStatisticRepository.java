package kr.or.kosa.cmsplusmain.domain.statics.repository;

import com.querydsl.core.types.dsl.Expressions;
import kr.or.kosa.cmsplusmain.domain.base.repository.V2BaseRepository;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.statics.dto.MemberContractStatisticDto;
import kr.or.kosa.cmsplusmain.domain.statics.dto.QMemberContractStatisticDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.contract;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.member;
import static kr.or.kosa.cmsplusmain.domain.payment.entity.QPayment.payment;


@Repository
public class MemberContractStatisticRepository extends V2BaseRepository<Member, Long> {

    public List<MemberContractStatisticDto> findMemberContractStatistic(Long vendorId) {
        return selectWithNotDel(new QMemberContractStatisticDto(
                contract.id,
                member.name,
                member.enrollDate.year(),
                Expressions.numberTemplate(Integer.class,
                        "DATEDIFF({0}, {1})",
                        contract.contractEndDate,
                        contract.contractStartDate
                ).as("contractDuration"),
                contract.contractPrice,
                payment.paymentType,
                contract.contractStartDate.between(
                        LocalDate.now().withDayOfMonth(1),
                        LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1)
                )), member, contract)
                .from(contract)
                .join(contract.member, member)
                .join(contract.payment, payment)
                .where(member.vendor.id.eq(vendorId))
                .fetch();
    }
}