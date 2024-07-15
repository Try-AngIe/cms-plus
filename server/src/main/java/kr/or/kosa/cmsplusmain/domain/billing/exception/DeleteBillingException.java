package kr.or.kosa.cmsplusmain.domain.billing.exception;

/*
* 청구 삭제 예외
* */
public class DeleteBillingException extends RuntimeException {
	public DeleteBillingException() {
		super("청구 삭제는 청구서 발송 전에만 가능합니다.");
	}
}
