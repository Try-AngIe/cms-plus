package kr.or.kosa.cmsplusmain.domain.payment.entity.method;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.payment.converter.PaymentMethodConverter;
import kr.or.kosa.cmsplusmain.domain.payment.converter.PaymentTypeConverter;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 결제 수단 정보
 *
 * 가상계좌: ...
 * 납부자결제: ...
 * 카드: 카드 번호, 카드 소유주 ...
 * CMS: 계좌 번호, 계좌 소유주 ...
 * */
@Comment("결제수단 세부정보")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "payment_method")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PaymentMethodInfo extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_method_info_id")
	private Long id;

	@Comment("결제수단")
	@Convert(converter = PaymentMethodConverter.class)
	@Column(name = "payment_method", updatable = false, insertable = false)
	private PaymentMethod paymentMethod;

	/*
	 * 결제수단 정보 삭제
	 * */
	@Override
	public void delete(){
		// TODO
		/*
		 * 결제 수단 정보 삭제에서 예외 케이스 있는지 고려
		 * 카드결제, 실시간CMS
		 * */
		super.delete();
	}
}
