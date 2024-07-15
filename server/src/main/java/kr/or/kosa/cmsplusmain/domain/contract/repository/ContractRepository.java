package kr.or.kosa.cmsplusmain.domain.contract.repository;

import org.springframework.stereotype.Repository;

import kr.or.kosa.cmsplusmain.domain.base.repository.BaseRepository;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;

@Repository
public interface ContractRepository extends BaseRepository<Contract, Long> {
}
