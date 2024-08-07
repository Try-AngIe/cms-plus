package kr.or.kosa.cmsplusbatch.domain.billing.entity;

import kr.or.kosa.cmsplusbatch.domain.billing.exception.InvalidBillingStatusException;
import kr.or.kosa.cmsplusbatch.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusbatch.util.FormatUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// TODO base interface

/**
 * 청구의 여러 동작들의 수행 가능 여부를 판단
 * */
@RequiredArgsConstructor
@Getter
public class BillingState {
	private final Field field;
	private final boolean isEnabled;
	private final String reason;

	public BillingState(Field field, Boolean isEnabled) {
		this.field = field;
		this.isEnabled = isEnabled;
		this.reason = null;
	}

	public enum Field {
		UPDATE {
			@Override
			public BillingState checkState(Billing billing) {
				if (billing.getBillingStatus() == BillingStatus.WAITING_PAYMENT) {
					return new BillingState(UPDATE, false,
						"[%s] 상태에서는 청구 수정이 불가능합니다".formatted(billing.getBillingStatus().getTitle()));
				}
				return new BillingState(UPDATE, true);
			}
		},
		DELETE {
			@Override
			public BillingState checkState(Billing billing) {
				if (billing.getBillingStatus() == BillingStatus.WAITING_PAYMENT) {
					return new BillingState(DELETE, false,
						"[%s] 상태에서는 청구 삭제가 불가능합니다".formatted(billing.getBillingStatus().getTitle()));
				}
				return new BillingState(DELETE, true);
			}
		},
		SEND_INVOICE {
			@Override
			public BillingState checkState(Billing billing) {
				if (billing.getBillingStatus() == BillingStatus.PAID) {
					return new BillingState(SEND_INVOICE, false,
						"[%s]상태에서는 청구서 발송이 불가능합니다".formatted(billing.getBillingStatus().getTitle()));
				}
				if (billing.getBillingStatus() == BillingStatus.WAITING_PAYMENT && billing.getInvoiceSendDateTime() != null) {
					return new BillingState(SEND_INVOICE, false,
						"[%s]에 이미 청구서가 발송되었습니다".formatted(FormatUtil.formatDateTime(billing.getInvoiceSendDateTime())));
				}
				return new BillingState(SEND_INVOICE, true);
			}
		},
		CANCEL_INVOICE {
			@Override
			public BillingState checkState(Billing billing) {
				if (billing.getInvoiceSendDateTime() == null) {
					return new BillingState(CANCEL_INVOICE, false,
						"발송된 청구서가 없습니다");
				}
				if (billing.getBillingStatus() == BillingStatus.PAID) {
					return new BillingState(CANCEL_INVOICE, false,
						"[%s] 상태에서는 청구서 발송 취소가 불가능합니다".formatted(billing.getBillingStatus().getTitle()));
				}
				return new BillingState(CANCEL_INVOICE, true);
			}
		},
		PAY_REALTIME {
			@Override
			public BillingState checkState(Billing billing) {
				if (billing.getBillingStatus() == BillingStatus.PAID) {
					return new BillingState(PAY_REALTIME, false,
						"이미 결제된 청구입니다");
				}
				Payment payment = billing.getContract().getPayment();
				if (!payment.canPayRealtime()) {
					return new BillingState(PAY_REALTIME, false,
						"현재 설정된 결제방식으로는 실시간 결제가 불가능합니다");
				}
				return new BillingState(PAY_REALTIME, true);
			}
		},
		PAY {
			@Override
			public BillingState checkState(Billing billing) {
				if (billing.getBillingStatus() == BillingStatus.PAID) {
					return new BillingState(PAY, false,
							"이미 결제된 청구입니다");
				}
				return new BillingState(PAY, true);
			}
		},
		CANCEL_PAYMENT {
			@Override
			public BillingState checkState(Billing billing) {
				if (billing.getPaidDateTime() == null || billing.getBillingStatus() != BillingStatus.PAID) {
					return new BillingState(PAY_REALTIME, false,
						"결제 내역이 없습니다");
				}
				Payment payment = billing.getContract().getPayment();
				if (!payment.canCancel()) {
					return new BillingState(CANCEL_PAYMENT, false,
						"현재 설정된 결제방식으로는 결제 취소가 불가능합니다");
				}
				return new BillingState(CANCEL_PAYMENT, true);
			}
		};

		public abstract BillingState checkState(Billing billing);

		public void validateState(Billing billing) {
			BillingState state = checkState(billing);
			if (!state.isEnabled()) {
				throw new InvalidBillingStatusException(state);
			}
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}
}