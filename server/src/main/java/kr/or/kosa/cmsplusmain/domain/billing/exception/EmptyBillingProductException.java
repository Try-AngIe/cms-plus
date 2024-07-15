package kr.or.kosa.cmsplusmain.domain.billing.exception;

public class EmptyBillingProductException extends RuntimeException {
	public EmptyBillingProductException() {
		super("청구는 최소 한개의 상품을 포함해야합니다.");
	}
}
