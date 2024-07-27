package kr.or.kosa.cmsplusmain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingProduct;
import kr.or.kosa.cmsplusmain.domain.billing.repository.BillingRepository;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractProduct;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractRepository;
import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import kr.or.kosa.cmsplusmain.domain.member.entity.Address;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.member.repository.MemberRepository;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.CardPaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.CmsPaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethodInfo;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.AutoPaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.BuyerPaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentTypeInfo;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.VirtualAccountPaymentType;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.entity.ProductStatus;
import kr.or.kosa.cmsplusmain.domain.product.repository.ProductRepository;
import kr.or.kosa.cmsplusmain.domain.settings.entity.SimpConsentSetting;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.UserRole;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import kr.or.kosa.cmsplusmain.domain.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleDataLoader {

	private final VendorRepository vendorRepository;
	private final ProductRepository productRepository;
	private final MemberRepository memberRepository;
	private final ContractRepository contractRepository;
	private final BillingRepository billingRepository;

	private final Random random = new Random();
	private final RandomGenerator randomGenerator = new RandomGenerator(random);

	@PostConstruct
	public void init() {
		Vendor vendor = vendorRepository.save(
			createVendorWithDefaultProduct(
				"vendor1",
				"테스트고객",
				"testvendor@fms.com",
				"01012341234")
		);


		generateSampleData(vendor, 100, 100, 100, 100);



	}

	public void generateSampleData(Vendor vendor, int productCnt, int memberCnt, int contractCnt, int billingCnt) {
		List<Product> products = createProductsForVendor(vendor, productCnt);
		List<Member> members = createMembersForVendor(vendor, memberCnt);
		List<Contract> contracts = createContractsForMembers(members, products, contractCnt);
		List<Billing> billings = createBillingsForContracts(contracts, products, billingCnt);

		productRepository.saveAll(products);
		memberRepository.saveAll(members);
		contractRepository.saveAll(contracts);
		billingRepository.saveAll(billings);
	}

	public List<Product> createProductsForVendor(Vendor vendor, int productCnt) {
		List<Product> products = new ArrayList<>(productCnt);
		for (int i = 0; i < productCnt; i++) {
			Product product = createTestProduct(vendor, i);
			products.add(product);
		}
		return products;
	}

	public List<Billing> createBillingsForContracts(List<Contract> contracts, List<Product> products, int count) {
		List<Billing> billings = new ArrayList<>();

		LocalDate billingDateFrom = LocalDate.of(2024, 1, 1);
		LocalDate billingDateTo = LocalDate.of(2024, 12, 27);

		for (int i = 0; i < count; i++) {
			Contract contract = randomGenerator.getRandomInList(contracts);
			LocalDate billingDate = randomGenerator.generateRandomDate(billingDateFrom, billingDateTo);
			int contractDay = random.nextInt(28) + 1;

			List<BillingProduct> billingProducts = createBillingProducts(products);

			Billing billing = new Billing(contract, randomGenerator.getRandomBillingType(), billingDate, contractDay, billingProducts);
			billing.setBillingStatus(randomGenerator.getRandomBillingStatus(billingDate));
			billing.setInvoiceMessage(randomGenerator.generateRandomInvoiceMessage());

			// 청구 상태 여부에 따른 결제 시각 및 청구서 발송 시각 설정
			switch (billing.getBillingStatus()) {
				case PAID -> billing.setPaidDateTime(billingDate.atTime(random.nextInt(23), random.nextInt(59)));
				case WAITING_PAYMENT, NON_PAID -> billing.setInvoiceSendDateTime(billingDate.atTime(random.nextInt(23), random.nextInt(59)));
			}

			billings.add(billing);
		}
		return billings;
	}

	public List<BillingProduct> createBillingProducts(List<Product> products) {
		List<BillingProduct> billingProducts = new ArrayList<>();
		int productCount = random.nextInt(3) + 1; // 1~3개의 상품 생성
		for (int i = 0; i < productCount; i++) {
			Product product = randomGenerator.getRandomInList(products);
			BillingProduct billingProduct = BillingProduct.builder()
				.product(product)
				.name(product.getName())
				.price(product.getPrice())
				.quantity(random.nextInt(5) + 1) // 1~5 사이의 수량
				.build();
			billingProducts.add(billingProduct);
		}
		return billingProducts;
	}

	public List<Member> createMembersForVendor(Vendor vendor, int count) {
		List<Member> createdMembers = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			Member member = createMember(vendor, i);
			member = memberRepository.save(member);
			createdMembers.add(member);
		}

		return createdMembers;
	}

	public Member createMember(Vendor vendor, int index) {
		String name = "학생" + (index + 1);
		String email = "student" + (index + 1) + "@example.com";
		String phone = randomGenerator.generateRandomPhone();
		String homePhone = randomGenerator.generateRandomHomePhone();
		Address address = new Address("12345", "서울시 강남구", "테헤란로 123");
		LocalDate enrollDate = LocalDate.now().minusDays(random.nextInt(365));

		return Member.builder()
			.vendor(vendor)
			.name(name)
			.email(email)
			.phone(phone)
			.homePhone(homePhone)
			.address(address)
			.memo("테스트 학생 메모" + (index + 1))
			.enrollDate(enrollDate)
			.invoiceSendMethod(MessageSendMethod.EMAIL)
			.autoInvoiceSend(true)
			.autoBilling(true)
			.build();
	}

	public Vendor createVendorWithDefaultProduct(String username, String name, String email, String phone) {
		// 간편동의 설정
		SimpConsentSetting simpConsentSetting = new SimpConsentSetting();
		simpConsentSetting.addPaymentMethod(PaymentMethod.CMS);
		simpConsentSetting.addPaymentMethod(PaymentMethod.CARD);

		// 고객
		Vendor vendor = createTestVendor(username, name, email, phone);
		vendor.setSimpConsentSetting(simpConsentSetting);
		vendor = vendorRepository.save(vendor);

		// 기본 상품
		Product defaultProduct =createTestProduct(vendor, -1);
		defaultProduct = productRepository.save(defaultProduct);

		// 간편동의 기본상품 추가
		vendor.getSimpConsentSetting().addProduct(defaultProduct);

		return vendorRepository.save(vendor);
	}

	public List<Contract> createContractsForMembers(List<Member> members, List<Product> products, int count) {
		List<Contract> createdContracts = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			Member member = randomGenerator.getRandomInList(members);
			Contract contract = createContract(member, i, products);
			createdContracts.add(contract);
		}

		return createdContracts;
	}

	public Contract createContract(Member member, int index, List<Product> products) {
		Payment payment = generatePayment();
		LocalDate startDate = LocalDate.now().minusMonths(random.nextInt(12));
		LocalDate endDate = startDate.plusMonths(3);

		ContractStatus status = (LocalDate.now().isAfter(endDate)) ? ContractStatus.DISABLED : ContractStatus.ENABLED;

		Contract contract = Contract.builder()
			.vendor(member.getVendor())
			.member(member)
			.contractName("계약" + (index + 1))
			.contractDay(random.nextInt(28) + 1)
			.payment(payment)
			.contractStatus(status)
			.contractStartDate(startDate)
			.contractEndDate(endDate)
			.build();

		addRandomContractProducts(contract, products);

		return contract;
	}

	public void addRandomContractProducts(Contract contract, List<Product> products) {
		int productCount = random.nextInt(3) + 1; // 1에서 3개의 상품 추가

		for (int i = 0; i < productCount && i < products.size(); i++) {
			Product product = products.get(i);
			ContractProduct contractProduct = ContractProduct.builder()
				.contract(contract)
				.product(product)
				.name(product.getName())
				.price(product.getPrice())
				.quantity(random.nextInt(5) + 1) // 1에서 5개의 수량
				.build();

			contract.addContractProduct(contractProduct);
		}
	}

	public Payment generatePayment() {
		PaymentType paymentType = getRandomPaymentType();
		PaymentMethod paymentMethod = getRandomPaymentMethod(paymentType);

		return Payment.builder()
			.paymentType(paymentType)
			.paymentTypeInfo(generatePaymentTypeInfo(paymentType))
			.paymentMethod(paymentMethod)
			.paymentMethodInfo(generatePaymentMethodInfo(paymentMethod))
			.build();
	}

	public PaymentType getRandomPaymentType() {
		return randomGenerator.getRandomInList(Arrays.stream(PaymentType.values()).toList());
	}

	public PaymentMethod getRandomPaymentMethod(PaymentType paymentType) {
		if (paymentType == PaymentType.AUTO) {
			return random.nextBoolean() ? PaymentMethod.CARD : PaymentMethod.CMS;
		} else {
			return null;
		}
	}

	public PaymentTypeInfo generatePaymentTypeInfo(PaymentType paymentType) {
		return switch (paymentType) {
			case AUTO -> AutoPaymentType.builder()
				.consentImgUrl("blob:http://example.com/consent" + random.nextInt(1000) + ".jpg")
				.simpleConsentReqDateTime(LocalDateTime.now().minusDays(random.nextInt(30)))
				.build();
			case BUYER -> BuyerPaymentType.builder()
				.availableMethods(Set.of(PaymentMethod.CARD, PaymentMethod.ACCOUNT))
				.build();
			case VIRTUAL -> VirtualAccountPaymentType.builder()
				.bank(randomGenerator.getRandomBank())
				.accountNumber(randomGenerator.generateRandomAccountNumber())
				.accountOwner("가상계좌주인" + random.nextInt(100))
				.build();
		};
	}

	public PaymentMethodInfo generatePaymentMethodInfo(PaymentMethod paymentMethod) {
		if (paymentMethod == null) {
			return null;
		}
		return switch (paymentMethod) {
			case CARD -> CardPaymentMethod.builder()
				.cardNumber(randomGenerator.generateRandomCardNumber())
				.cardMonth(random.nextInt(12) + 1)
				.cardYear(LocalDate.now().getYear() + random.nextInt(5))
				.cardOwner("카드주인" + random.nextInt(100))
				.cardOwnerBirth(LocalDate.now().minusYears(20 + random.nextInt(40)))
				.build();
			case CMS -> CmsPaymentMethod.builder()
				.bank(randomGenerator.getRandomBank())
				.accountNumber(randomGenerator.generateRandomAccountNumber())
				.accountOwner("CMS주인" + random.nextInt(100))
				.accountOwnerBirth(LocalDate.now().minusYears(20 + random.nextInt(40)))
				.build();
			case ACCOUNT ->
				CmsPaymentMethod.builder()
					.bank(randomGenerator.getRandomBank())
					.accountNumber(randomGenerator.generateRandomAccountNumber())
					.accountOwner("계좌주인" + random.nextInt(100))
					.accountOwnerBirth(LocalDate.now().minusYears(20 + random.nextInt(40)))
					.build();
		};
	}

	public Vendor createTestVendor(String username, String name, String email, String phone) {
		return Vendor.builder()
			.username(username)
			.password("password123!")
			.name(name)
			.email(email)
			.phone(phone)
			.homePhone("021235678")
			.department("영업부")
			.role(UserRole.ROLE_VENDOR)
			.build();
	}

	public Product createTestProduct(Vendor vendor, int idx) {
		return Product.builder()
			.vendor(vendor)
			.status(ProductStatus.ENABLED)
			.name("테스트 상품" + ((idx != -1) ? idx : ""))
			.price(random.nextInt(1000, 1000000))
			.memo("이것은 테스트 상품입니다. " + ((idx != -1) ? idx : ""))
			.build();
	}
}
