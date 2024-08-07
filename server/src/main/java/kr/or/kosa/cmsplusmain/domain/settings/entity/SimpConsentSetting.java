package kr.or.kosa.cmsplusmain.domain.settings.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.base.entity.BaseEntity;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;

@Entity
@Table(name = "setting_simpconsent")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpConsentSetting extends BaseEntity {

	/* 최대 간편동의 설정 상품 수 */
	private static final int MAX_SIMP_CONSENT_PRODUCTS = 3;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "setting_simpconsent_id")
	private Long id;

	@Setter
	@OneToOne(mappedBy = "simpConsentSetting", fetch = FetchType.LAZY, optional = false)
	@NotNull
	private Vendor vendor;

	/*간편동의 설정 결제수단*/
	@ElementCollection(targetClass = PaymentMethod.class, fetch = FetchType.LAZY)
	@CollectionTable(name = "simpconsent_vendor_auto_payment_method")
	@Enumerated(EnumType.STRING)
	@Column(name = "simpconsent_auto_payment_method")
	@Builder.Default
	private Set<PaymentMethod> simpConsentPayments =
		new HashSet<>(List.of(PaymentMethod.CMS, PaymentMethod.CARD));

	/*간편동의 설정 상품*/
	@ManyToMany
	@JoinTable(name = "simpconsent_vendor_product",
		joinColumns = @JoinColumn(name = "setting_simpconsent_id"),
		inverseJoinColumns = @JoinColumn(name = "product_id")
	)
	@SQLRestriction(BaseEntity.NON_DELETED_QUERY)
	private Set<Product> simpConsentProducts = new HashSet<>();

	public void addProduct(Product product) {
		this.simpConsentProducts.add(product);
	}

	public void addPaymentMethod(PaymentMethod paymentMethod) {
		if (!PaymentType.getAutoPaymentMethods().contains(paymentMethod)) {
			throw new IllegalArgumentException("간편동의는 자동결제 수단만 등록할 수 있습니다.");
		}
		this.simpConsentPayments.add(paymentMethod);
	}
}
