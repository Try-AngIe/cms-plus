package kr.or.kosa.cmsplusmain.domain.billing.exception;

/*
* 청구 결제일 수정 예외
* */
public class UpdateBillingDateException extends RuntimeException {

	public UpdateBillingDateException() {
		super("청구의 결제일은 청구서 발송 전에만 가능합니다.");
	}
}
