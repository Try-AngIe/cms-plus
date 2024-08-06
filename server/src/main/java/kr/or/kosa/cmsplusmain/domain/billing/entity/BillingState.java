package kr.or.kosa.cmsplusmain.domain.billing.entity;

import java.time.LocalDateTime;

import kr.or.kosa.cmsplusmain.domain.billing.exception.InvalidBillingStatusException;
import kr.or.kosa.cmsplusmain.domain.payment.entity.ConsentStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.AutoPaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentTypeInfo;
import kr.or.kosa.cmsplusmain.util.FormatUtil;
import kr.or.kosa.cmsplusmain.util.HibernateUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 청구의 여러 동작들의 수행 가능 여부를 판단 및 이유 리턴
 * 요구사항 : 사용자에게 특정 행동의 실패 이유를 보여준다.
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

	public boolean isEnabled() {
		return isEnabled;
	}

	public enum Field {
		UPDATE {
			@Override
			public BillingState checkState(Billing billing) {
				if (billing.getBillingStatus() == BillingStatus.WAITING_PAYMENT
					|| billing.getBillingStatus() == BillingStatus.PAID) {
					return new BillingState(UPDATE, false,
						"[%s] 상태에서는 청구 수정이 불가능합니다".formatted(billing.getBillingStatus().getTitle()));
				}
				return new BillingState(UPDATE, true);
			}
		},
		DELETE {
			@Override
			public BillingState checkState(Billing billing) {
				// 청구 삭제는 언제나 가능하다.
				// if (
				// 	billing.getBillingStatus() == BillingStatus.WAITING_PAYMENT
				// 	|| billing.getBillingStatus() == BillingStatus.PAID) {
				// 	return new BillingState(DELETE, false,
				// 		"[%s] 상태에서는 청구 삭제가 불가능합니다".formatted(billing.getBillingStatus().getTitle()));
				// }
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
				if (billing.getBillingStatus() == BillingStatus.WAITING_PAYMENT
					&& billing.getInvoiceSendDateTime() != null) {
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

				// 간편동의 여부
				PaymentTypeInfo paymentTypeInfo = payment.getPaymentTypeInfo();
				AutoPaymentType autoPaymentType = HibernateUtils.getImplements(paymentTypeInfo, AutoPaymentType.class);
				if (autoPaymentType == null) {
					throw new IllegalStateException("canPayRealtime에서 자동결제 여부가 판단되지 않아 결제 방식이 자동결제가 아닌 상태");
				}

				if (autoPaymentType.getConsentStatus() != ConsentStatus.ACCEPT) {
					return new BillingState(PAY_REALTIME, false,
						"동의가 필요합니다.");
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
					return new BillingState(CANCEL_PAYMENT, false,
						"결제 내역이 없습니다");
				}
				if (LocalDateTime.now().isAfter(billing.getPaidDateTime().plusMonths(1))) {
					return new BillingState(CANCEL_PAYMENT, false,
						"한 달이상 지난 결제는 취소가 불가능합니다");
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
}