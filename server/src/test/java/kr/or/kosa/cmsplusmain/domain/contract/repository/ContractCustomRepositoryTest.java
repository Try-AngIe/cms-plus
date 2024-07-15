package kr.or.kosa.cmsplusmain.domain.contract.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ContractCustomRepositoryTest {
	//
	// @Autowired
	// private ContractCustomRepository repository;
	//
	// @Autowired
	// private EntityManager em;
	//
	// @Test
	// @DisplayName("삭제된 계약은 조회되지 않는다.")
	// void shouldNotFetchedDeletedContracts() {
	//
	// 	// given
	// 	String vendorUsername = "vendor1";
	// 	ContractSearch contractSearch = new ContractSearch();
	// 	SortPageDto.Req pageable = new SortPageDto.Req();
	//
	// 	// when
	// 	List<Contract> before = repository.findContractListWithCondition(
	// 		vendorUsername,
	// 		contractSearch,
	// 		pageable);
	//
	// 	Contract removed = before.get(0);
	// 	removed.delete();
	// 	em.merge(removed);
	//
	// 	List<Contract> after = repository.findContractListWithCondition(
	// 		vendorUsername,
	// 		contractSearch,
	// 		pageable);
	//
	// 	// then
	// 	assertFalse(after.contains(removed));
	// 	assertEquals(before.size() - 1, after.size());
	// }
	//
	// private Contract createContract() {
	//
	// }
}