package kr.or.kosa.cmsplusmain.domain.contract.exception;

public class EmptyContractProductException extends RuntimeException {
	public EmptyContractProductException() {
		super("계약은 최소 한개의 상품을 포함해야합니다.");
	}
}
